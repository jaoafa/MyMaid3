/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

package com.jaoafa.MyMaid3.Lib;

public class CmdUsage {
    private final String command;
    private final Cmd[] commands;

    public CmdUsage(String command, Cmd... commands) {
        this.command = command;
        this.commands = commands;
    }

    public String getCommand() {
        return command;
    }

    public Cmd[] getCommands() {
        return commands;
    }

    public static class Cmd {
        private final String args;
        private final String details;

        public Cmd(String args, String details){
            this.args = args;
            this.details = details;
        }

        public String getArgs() {
            return args;
        }

        public String getDetails() {
            return details;
        }
    }
}
