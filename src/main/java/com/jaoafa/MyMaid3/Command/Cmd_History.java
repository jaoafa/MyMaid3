package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.Historyjao;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class Cmd_History extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        // jaoHistory

        if (args.length >= 2 && args[0].equalsIgnoreCase("status")) {
            // /history status mine_book000
            onCmd_Status(sender, cmd, args);
            return true;
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("add")) {
            // /history add mine_book000 test a b c
            onCmd_Add(sender, cmd, args);
            return true;
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("disable")) {
            // /history disable mine_book000 1
            onCmd_Disable(sender, cmd, args);
            return true;
        }

        SendUsageMessage(sender, getDescription(), getUsage());
        return true;
    }

    private void onCmd_Status(CommandSender sender, Command cmd, String[] args) {
        // /history status mine_book000
        if (sender instanceof Player) {
            Player commander = (Player) sender;
            if (!isAMR(commander)) {
                SendMessage(sender, cmd, ChatColor.GREEN + "このコマンドは、あなたの権限では使用できません。");
                return;
            }
        }
        OfflinePlayer offplayer = getOfflinePlayer(args[1]);
        Historyjao histjao = new Historyjao(offplayer);
        histjao.DBSync(true);
        if (histjao.isFound()) {
            SendMessage(sender, cmd, "jaoHistoryにデータが見つかりました。");
            for (Historyjao.HistoryData histdata : histjao.getHistoryDatas()) {
                SendMessage(sender, cmd, histdata.id + " - " + histdata.message
                        + " (" + sdfFormat(histdata.getCreatedAt()) + " | " + sdfFormat(histdata.getUpdatedAt()) + ")");
            }
        } else {
            SendMessage(sender, cmd, "jaoHistoryにデータが見つかりませんでした。");
        }
    }

    private void onCmd_Add(CommandSender sender, Command cmd, String[] args) {
        // /history add mine_book000 test a b c
        if (sender instanceof Player) {
            Player commander = (Player) sender;
            if (!isAMR(commander)) {
                SendMessage(sender, cmd, ChatColor.GREEN + "このコマンドは、あなたの権限では使用できません。");
                return;
            }
        }
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        OfflinePlayer offplayer = getOfflinePlayer(args[1]);
        Historyjao histjao = new Historyjao(offplayer);
        if (histjao.add(reason)) {
            SendMessage(sender, cmd, ChatColor.GREEN + "実行に成功しました。");
        } else {
            SendMessage(sender, cmd, ChatColor.GREEN + "実行に失敗しました。");
        }
    }

    private void onCmd_Disable(CommandSender sender, Command cmd, String[] args) {
        // /history disable mine_book000 1
        if (sender instanceof Player) {
            Player commander = (Player) sender;
            if (!isAMR(commander)) {
                SendMessage(sender, cmd, ChatColor.GREEN + "このコマンドは、あなたの権限では使用できません。");
                return;
            }
        }
        if (!isInt(args[2])) {
            SendMessage(sender, cmd, ChatColor.GREEN + "IDには数値を指定してください。");
            return;
        }
        OfflinePlayer offplayer = getOfflinePlayer(args[1]);
        int id = Integer.parseInt(args[2]);
        Historyjao histjao = new Historyjao(offplayer);
        if (histjao.disable(id)) {
            SendMessage(sender, cmd, ChatColor.GREEN + "実行に成功しました。");
        } else {
            SendMessage(sender, cmd, ChatColor.GREEN + "実行に失敗しました。");
        }
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer getOfflinePlayer(String name_or_uuid) {
        if (name_or_uuid.contains("-")) {
            return Bukkit.getOfflinePlayer(UUID.fromString(name_or_uuid));
        } else {
            return Bukkit.getOfflinePlayer(name_or_uuid);
        }
    }

    @Override
    public String getDescription() {
        return "jaoHistoryに関する処理を行います。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "history",
                new CmdUsage.Cmd("add <Target> <Message...>", "Targetに対してMessageのヒストリーを追加します。"),
                new CmdUsage.Cmd("disable <Target> <ID>", "TargetのIDに対応するヒストリーを無効にします。"),
                new CmdUsage.Cmd("status <Target>", "Targetのヒストリー情報を表示します。")
        );
    }
}
