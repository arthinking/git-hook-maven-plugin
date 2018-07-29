package com.itzhai.tools.enums;

/**
 * Git hook枚举
 *
 * Created by arthinking on 29/7/2018.
 * 详细Git hook介绍: https://git-scm.com/docs/githooks
 */
public enum HookTypeEnum {

    APPLY_PATCH_MSG("applypatch-msg", "git-am命令执行时被调用"),
    PRE_APPLYPATCH("pre-applypatch", "补丁被应用后还未提交前被调用"),
    POST_APPLYPATCH("post-applypatch", "补丁被应用并且在完成提交的情况下被调用"),
    PRE_COMMIT("pre-commit", "git-commit命令触发, 在得到提交消息和开始提交前被调用"),
    PREPARE_COMMIT_MSG("prepare-commit-msg", "在编辑器启动前, 默认提交消息准备好后被调用"),
    COMMIT_MSG("commit-msg", "git-commit命令执行时被调用"),
    POST_COMMIT("post-commit", "在一个提交完成时被调用, 主要用途是通知提示"),
    PRE_REBASE("pre-rebase", "当git-base命令执行时被调用; 主要目的是阻止那不应被rebase的分支被rebase(例如，一个已经发布的分支提交就不应被rebase)"),
    POST_CHECKOUT("post-checkout","当git-checkout命令更新完整个工作树(worktree)后被调用"),
    POST_MERGE("post-merge", "当git-merge命令执行时被调用"),
    PRE_PUSH("pre-push", "当git-push命令执行时被调用"),
    PRE_RECEIVE("pre-receive", "git-receive-pack命令执行时被调用"),
    UPDATE("update", "当用户在本地仓库执行git-push命令, 服务器上运行仓库就会执行git-receive-pack, git-rceve-pack会调用钩子"),
    POST_RECEIVE("post-receive", "当用户在本地仓库执行'git-push'命令时，服务器上运端仓库就会对应执行'git-receive-pack'。在所有远程仓库的引用(ref)都更新后，post-update 就会被调用。"),
    POST_UPDATE("post-update", "This hook is invoked by git-receive-pack[1] when it reacts to git push and updates reference(s) in its repository. It executes on the remote repository once after all the refs have been updated."),
    PRE_AUTO_GC("pre-auto-gc", "当调用'git-gc --auto'命令时，这个钩子(hook)就会被调用。它没有调用参数，如果钩子的执行結果是非零的话，那么'git-gc --auto'命令就会中止执行。"),
    POST_REWRITE("post-rewrite", "This hook is invoked by commands that rewrite commits (git-commit[1] when called with --amend and git-rebase[1]; currently git filter-branch does not call it!)");

    public static HookTypeEnum getByType(String type) {
        for (HookTypeEnum hookTypeEnum : values()) {
            if (hookTypeEnum.type.equals(type)) {
                return hookTypeEnum;
            }
        }
        return null;
    }

    String type;

    String desc;

    HookTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }


    @Override
    public String toString() {
        return type;
    }
}
