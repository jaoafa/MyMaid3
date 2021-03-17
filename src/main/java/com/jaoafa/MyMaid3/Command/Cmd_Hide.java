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
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Cmd_Hide extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }

        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはプレイヤーから実行してください。");
            return true;
        }
        Player player = (Player) sender;
        if (!isAMR(player)) {
            SendMessage(sender, cmd, "このコマンドは管理部・モデレータ・常連のみ使用できます。");
            return true;
        }
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.hidePlayer(Main.getJavaPlugin(), player);
        }

        if (!MyMaidConfig.isHid(player.getUniqueId())) {
            MyMaidConfig.addHid(player.getUniqueId());
        }

        SendMessage(sender, cmd, "あなたは他のプレイヤーから見えなくなりました。見えるようにするには/showを実行しましょう。");
        SendMessage(sender, cmd, "なお、プレイヤーリストからも見えなくなりますのでお気をつけて。");
        return true;
    }

    @Override
    public String getDescription() {
        return "姿を隠します。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "hide",
                new CmdUsage.Cmd("", getDescription())
        );
    }
}
