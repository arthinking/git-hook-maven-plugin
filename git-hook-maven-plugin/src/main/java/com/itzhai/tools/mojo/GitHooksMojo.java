package com.itzhai.tools.mojo;

import com.itzhai.tools.enums.HookTypeEnum;
import com.itzhai.tools.installer.GitHookInstaller;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Arrays;
import java.util.Map;

/**
 * Goals to set the git hook script
 *
 * Created by arthinking on 28/7/2018.
 */
@Mojo(name = "git-hooks")
public class GitHooksMojo extends AbstractMojo{

    @Parameter
    private Map<String, String> ghooks;


    public void execute() throws MojoExecutionException, MojoFailureException {

        if(null == ghooks) {
            throw new MojoExecutionException("Please config your git hook info in the pom file!");
        }

        for (Map.Entry<String, String> entry : ghooks.entrySet()) {

            if (null == entry.getValue() || "".equals(entry.getValue().trim())) {
               continue;
            }

            if (null != HookTypeEnum.getByType(entry.getKey())) {
                getLog().info("========== " + entry.getKey() + " ==========");
                new GitHookInstaller(getLog(), entry.getKey(), entry.getValue()).installGitHook();
            } else {
                throw new MojoExecutionException(String.format("Git hook type not fund: %s, expected types: %s",
                        entry.getKey(), Arrays.toString(HookTypeEnum.values())));
            }

        }
    }

}
