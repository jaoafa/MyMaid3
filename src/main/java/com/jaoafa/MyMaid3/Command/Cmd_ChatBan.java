package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.ChatBan;
import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Cmd_ChatBan extends MyMaidLibrary implements CommandExecutor, CommandPremise, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        if ((sender instanceof Player) && !isAM((Player) sender)) {
            SendMessage(sender, cmd, "このコマンドはあなたの権限では実行できません。");
            return true;
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("add")) {
            // /chatban add <PlayerName> <Reason>
            ChatBan chatban;
            if (MyMaidLibrary.isUUID(args[1])) {
                chatban = new ChatBan(UUID.fromString(args[1]));
            } else {
                chatban = new ChatBan(Bukkit.getOfflinePlayer(args[1]));
            }
            if (chatban.isBanned()) {
                SendMessage(sender, cmd, "指定されたユーザーは既に処罰済みです。");
                return true;
            }
            String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            boolean bool = chatban.addBan(sender.getName(), reason);
            SendMessage(sender, cmd, "処罰に" + (bool ? "成功" : "失敗") + "しました。");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            // /chatban remove <PlayerName>
            ChatBan chatban;
            if (MyMaidLibrary.isUUID(args[1])) {
                chatban = new ChatBan(UUID.fromString(args[1]));
            } else {
                chatban = new ChatBan(Bukkit.getOfflinePlayer(args[1]));
            }
            if (!chatban.isBanned()) {
                SendMessage(sender, cmd, "指定されたユーザーは現在処罰されていません。");
                return true;
            }
            boolean bool = chatban.removeBan(sender.getName());
            SendMessage(sender, cmd, "処罰の解除に" + (bool ? "成功" : "失敗") + "しました。");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("status")) {
            // /chatban status <PlayerName>
            ChatBan chatban = MyMaidLibrary.isUUID(args[1]) ?
                    new ChatBan(UUID.fromString(args[1])) :
                    new ChatBan(Bukkit.getOfflinePlayer(args[1]));
            SendMessage(sender, cmd, "指定されたプレイヤー「" + chatban.getName() + "」は処罰" + (chatban.isBanned() ? ChatColor.RED + "されています" : ChatColor.GREEN + "されていません") + ChatColor.GREEN + "。");

            if (chatban.isBanned()) {
                SendMessage(sender, cmd, "Reason: " + chatban.getLastBanReason());
                SendMessage(sender, cmd, "Banned By: " + chatban.getBannedBy());
                SendMessage(sender, cmd, "Banned Date: " + MyMaidLibrary.sdfFormat(chatban.getBannedDate()));
                SendMessage(sender, cmd, "Banned By: " + MyMaidLibrary.sdfFormat(new Date(chatban.getDBSyncTime())));
            }
            return true;
        }
        SendUsageMessage(sender, getDescription(), getUsage());
        return true;
    }

    @Override
    public String getDescription() {
        return "ChatJailを制御します。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "chatban",
                new CmdUsage.Cmd("add <PlayerName> <Reason>", "プレイヤーをChatJailします。"),
                new CmdUsage.Cmd("remove <PlayerName>", "プレイヤーのChatJailを解除します。"),
                new CmdUsage.Cmd("status <PlayerName>", "プレイヤーのChatJail情報を表示します。")
        );
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            if (args[0].length() == 0) {
                return Arrays.asList("add", "remove", "list");
            } else {
                if ("add".startsWith(args[0])) {
                    return Collections.singletonList("add");
                } else if ("remove".startsWith(args[0])) {
                    return Collections.singletonList("remove");
                } else if ("list".startsWith(args[0])) {
                    return Collections.singletonList("list");
                }
            }
        }
        return Main.getJavaPlugin().onTabComplete(sender, command, alias, args);
    }
}
