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

import com.jaoafa.MyMaid3.Lib.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Cmd_Tpalias extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                // /tpalias list
                SendMessage(sender, cmd, "----- TeleportAlias List -----");
                for (Map.Entry<String, String> one : TeleportAlias.getAlias().entrySet()) {
                    SendMessage(sender, cmd, one.getKey() + " -> " + one.getValue());
                }
                return true;
            }
        }
        if (sender instanceof Player) {
            String group = PermissionsManager.getPermissionMainGroup((Player) sender);
            if (!group.equalsIgnoreCase("Moderator") && !group.equalsIgnoreCase("Admin")) {
                SendMessage(sender, cmd, "あなたはこのコマンドを使用できません。");
                return true;
            }
        } else {
            SendMessage(sender, cmd, "このコマンドはゲーム内またはコンソールから実行してください。");
            return true;
        }
        //Player player = (Player) sender;
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                boolean res = TeleportAlias.removeAlias(args[1]);
                SendMessage(sender, cmd, "削除に" + (res ? "成功" : "失敗") + "しました。");
                return true;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                boolean res = TeleportAlias.setAlias(args[1], args[2]);
                SendMessage(sender, cmd, "追加に" + (res ? "成功" : "失敗") + "しました。");
                return true;
            }
        }
        SendUsageMessage(sender, getDescription(), getUsage());
        return true;
    }

    @Override
    public String getDescription() {
        return "TeleportAliasの設定をします。Admin・Moderatorのみ利用できます。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "tpalias",
                new CmdUsage.Cmd("set <Target> <Replacement>", "tpコマンド実行時に<Target>を<Replacement>に置き換えるよう設定します。"),
                new CmdUsage.Cmd("remove <Target>", "tpコマンド実行時の<Target>置き換え設定を削除します。"),
                new CmdUsage.Cmd("list", "tpコマンド実行時の置き換え設定一覧を表示します。")
        );
    }
}
