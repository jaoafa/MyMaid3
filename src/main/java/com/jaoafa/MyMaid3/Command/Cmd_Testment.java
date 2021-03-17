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
import com.jaoafa.MyMaid3.Lib.Jail;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Cmd_Testment extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはサーバ内から実行できます。");
            return true;
        }
        Player commander = (Player) sender;
        Jail jail = new Jail(commander);
        if (!jail.isBanned()) {
            SendMessage(sender, cmd, ChatColor.GREEN + "あなたはJailされていないので遺言を書くことはできません。");
            return true;
        }
        if (jail.getLastBanTestment() != null) {
            SendMessage(sender, cmd, ChatColor.GREEN + "あなたは既に遺言を記録しています。");
            return true;
        }
        String testment = String.join(" ", args);
        if (jail.setTestment(testment)) {
            SendMessage(sender, cmd, ChatColor.GREEN + "遺言を記録しました。");
        } else {
            SendMessage(sender, cmd, ChatColor.GREEN + "遺言の記録に失敗しました。すでに遺言を記録しているかもしれません。");
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "遺言を記録します。/jail testmentのエイリアスコマンドです。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "testment",
                new CmdUsage.Cmd("", "遺言を記録します。")
        );
    }
}
