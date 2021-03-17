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

package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Cmd_Link extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        sender.sendMessage(
                "linkコマンドはMinecraftサーバ内ではなくjMS Gamers ClubのDiscordサーバ内で実行してね！そこでコマンドが発行されるから、そのコマンドをMinecraftサーバ内で打ち込んでね！");
        return true;
    }

    @Override
    public String getDescription() {
        return "linkコマンドはjMS Gamers Clubで実行するのだ。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "link",
                new CmdUsage.Cmd("", getDescription())
        );
    }
}
