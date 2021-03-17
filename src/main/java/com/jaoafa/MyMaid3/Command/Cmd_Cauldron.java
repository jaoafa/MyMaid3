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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cmd_Cauldron extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    public static final Map<UUID, Integer> cauldrons = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            String level;
            switch (args[0]) {
                case "off":
                    cauldrons.remove(player.getUniqueId());
                    level = "オフ";
                    break;
                case "1":
                    cauldrons.put(player.getUniqueId(), 1);
                    level = "1レベル";
                    break;
                case "2":
                    cauldrons.put(player.getUniqueId(), 2);
                    level = "2レベル";
                    break;
                case "3":
                    cauldrons.put(player.getUniqueId(), 3);
                    level = "3レベル";
                    break;
                default:
                    SendMessage(sender, cmd, "第1引数には「1・2・3・off」のいずれかを指定してください。");
                    return true;
            }
            SendMessage(sender, cmd, String.format("大釜設置時の水状態を「%s」に変更しました。", level));
            return true;
        }
        String level = "オフ";
        if (cauldrons.containsKey(player.getUniqueId())) {
            level = cauldrons.get(player.getUniqueId()) + "レベル";
        }
        SendMessage(sender, cmd, String.format("大釜設置時の水状態は「%s」です。", level));
        return true;
    }

    @Override
    public String getDescription() {
        return "大釜設置時の水状態を設定します。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "cauldron",
                new CmdUsage.Cmd("", "現在の大釜設置時の水状態を表示します。"),
                new CmdUsage.Cmd("[1-3]", "大釜設置時の水状態を1・2・3のいずれか指定された値に変更します。"),
                new CmdUsage.Cmd("off", "大釜設置時の水状態をオフ(0)にします。")
        );
    }
}
