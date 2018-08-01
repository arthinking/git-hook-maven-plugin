package com.itzhai.tools.installer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * git hook 脚本安装器
 *
 * Created by arthinking on 28/7/2018.
 */
public class GitHookInstaller {

    /**
     * 本地项目git文件夹名称
     */
    private static final String FILE_NAME_GIT = ".git";

    /**
     * 本地项目git hook文件夹名称
     */
    private static final String FILE_NAME_HOOKS = "hooks";

    /**
     * git hook 默认版本号, 如果是该版本号,则会进行备份更新
     */
    private static final String DEFAULT_VERSION = "0.0.0";

    /**
     * 本地项目自定义git hook脚本文件夹名称
     */
    private static final String FILE_NAME_LOCAL_HOOKS = "ghooks";

    /**
     * git hook名称
     */
    private String gitHookName;

    /**
     * git hook脚本配置路径
     */
    private String gitHookScriptPath;

    /**
     *  maven plugin 日志输出器
     */
    private Log log;

    public GitHookInstaller(Log log, String gitHookName, String gitHookScriptPath) {
        this.log = log;
        this.gitHookName = gitHookName;
        this.gitHookScriptPath = gitHookScriptPath;
    }

    /**
     * 安装git hook脚本
     *
     * @throws MojoExecutionException mojo异常
     */
    public void installGitHook() throws MojoExecutionException {

        // 获取git hook文件夹
        File gitHooksFolder = getGitHookFolder();

        // 判断时是否git项目
        checkIsGitRepo(gitHooksFolder);

        // 获取当前处理的git hook的完整路径
        File gitHookFile = new File(gitHooksFolder + File.separator + this.gitHookName);

        try {

            if (gitHookFile.exists()) {

                String newGitHookFileVersion = getGitHookFileVersion(getNewGitHookScriptInputStream());
                String currentGitHookFileVersion = getGitHookFileVersion(new FileInputStream(gitHookFile));

                log.info("[" + this.gitHookName + "]: " + currentGitHookFileVersion + "==>" + newGitHookFileVersion);

                if (DEFAULT_VERSION.equals(currentGitHookFileVersion) || !currentGitHookFileVersion.equals(newGitHookFileVersion)) {

                    backupHookFile(gitHooksFolder, gitHookFile, currentGitHookFileVersion);

                    installHookFile(gitHooksFolder);

                } else {
                    log.info(String.format("[%s]Latest version, no need to update!", this.gitHookName));
                }
            } else {
                installHookFile(gitHooksFolder);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error where installGitHook: ", e);
        }
    }

    /**
     * 获取最新的钩子脚本输入流
     *
     * @return InputStream
     * @throws FileNotFoundException
     * @throws MojoExecutionException
     */
    private InputStream getNewGitHookScriptInputStream() throws FileNotFoundException, MojoExecutionException {

        // 尝试从当前项目路径获取
        File localGitHookScript = new File(System.getProperty("user.dir") + File.separator + FILE_NAME_LOCAL_HOOKS
                + File.separator + this.gitHookScriptPath);
        if(localGitHookScript.exists()) {
            log.info("Use local git hook file: " + gitHookScriptPath);
            return new FileInputStream(localGitHookScript);
        }

        InputStream inputStream = GitHookInstaller.class.getClassLoader().getResourceAsStream(this.gitHookScriptPath);
        if(null != inputStream){
            log.info("Use plugin git hook file: " + gitHookScriptPath);
            return inputStream;
        }

        throw new MojoExecutionException("Git hook File not found: " + gitHookScriptPath);
    }

    /**
     * 判断是否git项目
     *
     * @param gitHooksFolder git hook folder
     * @throws MojoExecutionException
     */
    private void checkIsGitRepo(File gitHooksFolder) throws MojoExecutionException {
        if(!gitHooksFolder.exists()) {
            log.error("This is not a git repository, initial git hooks fail...");
            throw new MojoExecutionException("This is not a git repository, initial git hooks fail...");
        }
    }

    /**
     * 安装git hook脚本文件到git仓库中
     *
     * @param gitHooksFolder git hook folder
     * @throws MojoExecutionException
     * @throws FileNotFoundException
     */
    private void installHookFile(File gitHooksFolder) throws MojoExecutionException, FileNotFoundException {
        log.info(String.format("Install git hook[%s]...", this.gitHookName));
        writeFile(gitHooksFolder + File.separator + this.gitHookName, getNewGitHookScriptInputStream());
    }

    /**
     * 备份git hook脚本文件
     *
     * @param gitHooksFolder git hook folder
     * @param gitHookFile git hook file
     * @param currentGitHookScriptVersion 当前git hoook script版本
     * @throws MojoExecutionException
     */
    private void backupHookFile(File gitHooksFolder, File gitHookFile, String currentGitHookScriptVersion) throws MojoExecutionException {
        log.info(String.format("Backup git hook[%s]...", this.gitHookName));
        if (gitHookFile.renameTo(new File(gitHooksFolder + File.separator + this.gitHookName + "."
                + currentGitHookScriptVersion))) {
            log.info("Old commit-msg hook moved successful!");
        } else {
            log.info("Old commit-msg hook moved failed!");
            throw new MojoExecutionException("Old commit-msg hook moved failed!");
        }
    }

    private void writeFile(String fileFullPath, InputStream source) throws MojoExecutionException {
        try {
            StringBuilder sourceString = new StringBuilder();
            byte[] b = new byte[1024];
            for (int n; (n = source.read(b)) != -1;)   {
                sourceString.append(new String(b, 0, n));
            }
            FileUtils.fileWrite(fileFullPath, sourceString.toString());
            Runtime.getRuntime().exec("chmod +x " + fileFullPath);
        } catch (IOException e) {
            throw new MojoExecutionException("Error when writeFile", e);
        } finally {
            if(source != null)
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private File getGitHookFolder() {
       return new File(System.getProperty("user.dir") + File.separator + FILE_NAME_GIT + File.separator + FILE_NAME_HOOKS);
    }

    private String getGitHookFileVersion(InputStream inputStream) throws MojoExecutionException {

        if (inputStream == null) {
            throw new IllegalArgumentException("Illegal argument where execute getGitHookFileVersion, " +
                    "inputStream is null");
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            Pattern p = Pattern.compile("hook\\.version@(\\d+(\\.\\d+)*)");
            String line;
            while ((line = reader.readLine()) != null) {
                // 匹配版本号
                Matcher m = p.matcher(line);
                if (m.find()) {
                    return m.group(1);
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error when getGitHookFileVersion", e);
        } finally {
            try {
                if(reader != null)
                    reader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return DEFAULT_VERSION;
    }

}
