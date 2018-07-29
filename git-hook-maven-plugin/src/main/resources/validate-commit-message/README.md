# Validate commit message

> 该脚本用于`commit-msg`钩子, 主要用于为提交日志提供一个规范。

## 关于提交日志规范

良好的Commit Message有利于代码审查，能更快速查找变更记录，并且可以直接生成Change log。

Commit Message的写法规范：[AngularJS Git Commit Message Conventions](https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit#)的格式。

为了规范代码提交的说明，这里我们使用angular的规范写法：

```xml
<type>(<scope>): <subject>
<空行>
<body>
<空行>
<footer>
```

其中 head（<type>(<scope>): <subject>）是必须的，body和footer是可选的。
如果只需要输入header，可以直接使用：

```bash
git commit -m
```
命令提交。

如果需要输入body、footer这样的多行日志，需要输入：
```bash
git commit
```
跳出文本编辑器进行编写。

### Header
#### Type

commit的类别，包涵以下七种：

```bash
feat：新功能（feature）
fix：修补bug
docs：文档（documentation）
style： 格式（不影响代码运行的变动）
refactor：重构（即不是新增功能，也不是修改bug的代码变动）
test：增加测试
chore：构建过程或辅助工具的变动
```

#### Scope

commit的影响范围，比如会影响到哪个模块/性能/哪一层（业务层，持久层，缓存，rpc），如果是特性代码，可以写特性名称

#### Subject

commit的简短描述，不超过50个字符。

* 使用现在式，祈使句
* 第一个字母无需大写
* 结尾不用加句号

### Body
跟subject一样，使用现在式，祈使句。应该说明提交代码的动机，以及跟前一个版本的对比。

### Footer
Foot包含可以包含以下信息：

#### 描述重大变更的信息
以 BREAKING CHANGE 开头，后面是变更的具体描述，如

```
BREAKING CHANGE: isolate scope bindings definition has changed and
    the inject option for the directive controller injection was removed.

    To migrate the code follow the example below:

    Before:

    scope: {
      myAttr: 'attribute',
      myBind: 'bind',
      myExpression: 'expression',
      myEval: 'evaluate',
      myAccessor: 'accessor'
    }

    After:

    scope: {
      myAttr: '@',
      myBind: '@',
      myExpression: '&',
      // myEval - usually not useful, but in cases where the expression is assignable, you can use '='
      myAccessor: '=' // in directive's template change myAccessor() to myAccessor
    }

    The removed `inject` wasn't generaly useful for directives so there should be no code using it.
```


#### 关闭JIRA

如：
```bash
Closes DB-1001, DB1002
```

### 一些 Commit Message 例子：

```
feat($browser): onUrlChange event (popstate/hashchange/polling)

Added new event to $browser:
- forward popstate event if available
- forward hashchange event if popstate not available
- do polling when neither popstate nor hashchange available

Breaks $browser.onHashChange, which was removed (use onUrlChange instead)

---------

fix($compile): couple of unit tests for IE9

Older IEs serialize html uppercased, but IE9 does not...
Would be better to expect case insensitive, unfortunately jasmine does
not allow to user regexps for throw expectations.

Closes #392
Breaks foo.bar api, foo.baz should be used instead

---------

eat(directive): ng:disabled, ng:checked, ng:multiple, ng:readonly, ng:selected

New directives for proper binding these attributes in older browsers (IE).
Added coresponding description, live examples and e2e tests.

Closes #351

---------

feat($compile): simplify isolate scope bindings

Changed the isolate scope binding options to:
  - @attr - attribute binding (including interpolation)
  - =model - by-directional model binding
  - &expr - expression execution binding

This change simplifies the terminology as well as
number of choices available to the developer. It
also supports local name aliasing from the parent.

BREAKING CHANGE: isolate scope bindings definition has changed and
the inject option for the directive controller injection was removed.

To migrate the code follow the example below:

Before:

scope: {
  myAttr: 'attribute',
  myBind: 'bind',
  myExpression: 'expression',
  myEval: 'evaluate',
  myAccessor: 'accessor'
}

After:

scope: {
  myAttr: '@',
  myBind: '@',
  myExpression: '&',
  // myEval - usually not useful, but in cases where the expression is assignable, you can use '='
  myAccessor: '=' // in directive's template change myAccessor() to myAccessor
}

The removed `inject` wasn't generaly useful for directives so there should be no code using it.

```

如果是特性开发，则可以这样：

```bash
feat(短视频播放优化): 全屏播放动画效果优化
```