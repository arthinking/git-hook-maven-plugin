# Git hook maven plugin

Git可以在特定动作执行时触发自定义钩子脚本, 包括服务端脚本和客户端脚本。具体的钩子脚本编写说明可以参考: [自定义 Git - Git 钩子](https://www.git-scm.com/book/zh/v2/%E8%87%AA%E5%AE%9A%E4%B9%89-Git-Git-%E9%92%A9%E5%AD%90)

其中服务端钩子脚本可以统一在服务端配置, 针对所有用户都生效。而客户端脚本存在以下问题:

* 脚本存储于客户端, 并且无法纳入git管理, 每个人的脚本可以随意修改, 无法做到团队某些规范的推行;
* 一些优秀的脚本很难推广, 通过一种业界标准形式集成到项目中。

为此, 推出了该插件, 包括以下特点:

* 很方便的在项目中自定义团队的工作流, 把自定义钩子钩子脚本纳入git管理类, 方便团队共享;
* 把钩子脚本的安装集成到`Maven`的生命周期中的某个阶段, 方便的通过项目编译自动安装或者升级脚本;
* 提供通用的内置脚本, 方便钩子的配置, 目前只提供了`validate-commit-message`钩子脚本, 用于提交日志的规范, 遵循[AngularJS Git Commit Message Conventions](https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit#)的格式。

## 1、安装

从github上面下载该maven插件:

> git clone https://github.com/arthinking/git-hook-maven-plugin.git

该仓库包含两个项目:

> git-hook-maven-plugin
> git-hook-maven-plugin-demo

第一个项目是插件项目, 第二个项目是插件使用demo项目

通过`maven`安装`git-hook-maven-plugin`插件项目到本地仓库:

> mvn install

或者,您也可以推送到私服:

> mvn deploy

注意：deploy的时候，记得在项目的pom文件中配置私服地址：

```
<distributionManagement>
		<repository>
			<id>my-releases</id>
			<name>Nexus Release Repository</name>
			<url>http://localhost/nexus/content/repositories/thirdparty</url>
		</repository>
		<snapshotRepository>
			<id>my-snapshots</id>
			<name>Nexus Snapshot Repository</name>
			<url>http://localhost/nexus/content/repositories/snapshots</url>
		</snapshotRepository>
	</distributionManagement>
```

## 2、设置

### 2.1、在Maven项目中引入插件

在maven项目中引入插件:

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.itzhai.tools</groupId>
        <artifactId>git-hook-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <goals>
              <goal>git-hooks</goal>
            </goals>

            <configuration>
              <ghooks>
                <commit-msg>validate-commit-message/validate-commit-message.sh</commit-msg>
                <pre-commit>validate-code/validate-code.sh</pre-commit>
              </ghooks>
            </configuration>

            <phase>compile</phase>

          </execution>

        </executions>
      </plugin>
      ...
    </plugins>
  </build>
```

### 2.2、插件`configuration`配置说明

#### 2.2.1、ghooks标签

在该标签下面配置git hook。

#### 2.2.2、commit-msg标签

该标签是git hook钩子标签，目前可用的钩子标签如下：

```
applypatch-msg
pre-push
commit-msg
pre-rebase
post-update
prepare-commit-msg
pre-applypatch
update
pre-commit
```

### 2.2.1、配置内置钩子脚本

目前提供的内置钩子脚本有:

* [validate-commit-message/validate-commit-message.sh](https://github.com/arthinking/git-hook-maven-plugin/tree/master/git-hook-maven-plugin/src/main/resources/validate-commit-message)

`validate-commit-message/validate-commit-message.sh`钩子脚本, 该脚本主要用于校验提交日志的格式规范, 遵循[AngularJS Git Commit Message Conventions](https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit#)的格式。

一般在`ghooks`标签中配置git hook脚本标签, 上面例子中配置了`commit-msg`和`pre-commit`钩子标签, 内置的钩子脚本, 可以直接配置使用:

```xml
<commit-msg>validate-commit-message/validate-commit-message.sh</commit-msg>
```

钩子标签值的格式:

> 钩子脚本所在文件夹/脚本文件

### 2.2.2、配置自定义钩子脚本

当然, 您也可以配置自己编写的钩子脚本, 脚本的可以是python, shell, perl等格式。在git项目根目录创建一个`ghooks`文件夹把脚本文件放置到该文件夹中:

```
git-hook-maven-plugin-demo
  |-ghooks
    |-validate-code
      |--validate-code.sh
      |--README.md
  |--src
    |--main
      |--java
        ...
```
如上目录结构, 假设该脚本是`pre-commit`钩子, 则可以这样配置:

```xml
<pre-commit>validate-code/validate-code.sh</pre-commit>
```

建议自定义脚本放置到单独的文件夹中, 如上面的`validate-code`文件夹, 同时提供`README`文件, 阐述脚本的用途, 以及适用于什么钩子。

> 注意: 自己编写脚本的时候, 需要在脚本文件头的注释中声明脚本的版本, 插件检查到脚本版本有变更, 会自动更新项目的钩子脚本。格式如:

```python
! /usr/bin/env python
# Just for pre-commit hook
# hook.version@1.0.2
import sys,os,re
```

详细说明参考: [Git hook介绍](https://git-scm.com/docs/githooks)

## 3、运行

Maven插件的运行依赖于插件配置中的`phase`标签, 该标签声明了此插件会在Maven生命周期的哪一个阶段执行, 比如上面的例子就会在编译期间自动触发。


# References

- [自定义 Git - Git 钩子](https://www.git-scm.com/book/zh/v2/%E8%87%AA%E5%AE%9A%E4%B9%89-Git-Git-%E9%92%A9%E5%AD%90)
- [Plugin Developers Centre](https://maven.apache.org/plugin-developers/index.html)
