package edu.pa.web.prts.util.enums;

/**
 * Shell for executing shell scripts.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-04
 */
public enum ShellPath {

    // When execute node analysis process on linux.
    LINUX_SHELL("/bin/bash"),

    // When execute node analysis process on Windows. I use git bash at present.

    WINDOWS_GIT_BASH("C:/Program Files/Git/git-bash.exe")
    ;

    private String path;

    ShellPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
