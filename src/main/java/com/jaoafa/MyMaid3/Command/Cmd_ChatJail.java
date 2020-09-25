package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.ChatJail;
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

import java.util.*;

public class Cmd_ChatJail extends MyMaidLibrary implements CommandExecutor, CommandPremise, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
            return true;
        }
        if ((sender instanceof Player) && !isAM((Player) sender)) {
            SendMessage(sender, cmd, "このコマンドはあなたの権限では実行できません。");
            return true;
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("add")) {
            // /chatjail add <PlayerName> <Reason>
            ChatJail chatjail;
            if (MyMaidLibrary.isUUID(args[1])) {
                chatjail = new ChatJail(UUID.fromString(args[1]));
            } else {
                chatjail = new ChatJail(Bukkit.getOfflinePlayer(args[1]));
            }
            if (chatjail.isBanned()) {
                SendMessage(sender, cmd, "指定されたユーザーは既に処罰済みです。");
                return true;
            }
            String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            boolean bool = chatjail.addBan(sender.getName(), reason);
            SendMessage(sender, cmd, "処罰に" + (bool ? "成功" : "失敗") + "しました。");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            // /chatjail remove <PlayerName>
            ChatJail chatjail;
            if (MyMaidLibrary.isUUID(args[1])) {
                chatjail = new ChatJail(UUID.fromString(args[1]));
            } else {
                chatjail = new ChatJail(Bukkit.getOfflinePlayer(args[1]));
            }
            if (!chatjail.isBanned()) {
                SendMessage(sender, cmd, "指定されたユーザーは現在処罰されていません。");
                return true;
            }
            boolean bool = chatjail.removeBan(sender.getName());
            SendMessage(sender, cmd, "処罰の解除に" + (bool ? "成功" : "失敗") + "しました。");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("status")) {
            // /chatjail status <PlayerName>
            ChatJail chatjail;
            if (MyMaidLibrary.isUUID(args[1])) {
                chatjail = new ChatJail(UUID.fromString(args[1]));
            } else {
                chatjail = new ChatJail(Bukkit.getOfflinePlayer(args[1]));
            }
            SendMessage(sender, cmd, "指定されたプレイヤー「" + chatjail.getName() + "」は処罰" + (chatjail.isBanned() ? ChatColor.RED + "されています" : ChatColor.GREEN + "されていません") + ChatColor.GREEN + "。");

            if (chatjail.isBanned()) {
                SendMessage(sender, cmd, "Reason: " + chatjail.getLastBanReason());
                SendMessage(sender, cmd, "Banned By: " + chatjail.getBannedBy());
                SendMessage(sender, cmd, "Banned Date: " + MyMaidLibrary.sdfFormat(chatjail.getBannedDate()));
                SendMessage(sender, cmd, "Banned By: " + MyMaidLibrary.sdfFormat(new Date(chatjail.getDBSyncTime())));
            }
            return true;
        }
        SendUsageMessage(sender, cmd);
        return true;
    }

    @Override
    public String getDescription() {
        return "ChatJailを制御します。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/chatjail add <PlayerName> <Reason>");
                add("/chatjail remove <PlayerName>");
                add("/chatjail status <PlayerName>");
            }
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (args[0].length() == 0) {
                return Arrays.asList("add", "remove", "list");
            } else {
                //入力されている文字列と先頭一致
                if ("add".startsWith(args[0])) {
                    return Collections.singletonList("add");
                } else if ("remove".startsWith(args[0])) {
                    return Collections.singletonList("remove");
                } else if ("list".startsWith(args[0])) {
                    return Collections.singletonList("list");
                }
            }
        }
        //JavaPlugin#onTabComplete()を呼び出す
        return Main.getJavaPlugin().onTabComplete(sender, command, alias, args);
    }
}
