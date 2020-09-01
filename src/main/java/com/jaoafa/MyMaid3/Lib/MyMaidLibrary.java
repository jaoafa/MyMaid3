package com.jaoafa.MyMaid3.Lib;

import com.jaoafa.MyMaid3.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyMaidLibrary {
    @Nullable
    public static JavaPlugin JavaPlugin() {
        return Main.getJavaPlugin();
    }

    /**
     * Playerに対してメッセージを送信します。
     *
     * @param player  Player
     * @param cmdName コマンド名
     * @param message メッセージ
     */
    public static void SendMessage(Player player, String cmdName, String message) {
        player.sendMessage("[" + cmdName + "] " + ChatColor.GREEN + message);
    }

    /**
     * CommandSenderに対してメッセージを送信します。
     *
     * @param sender  CommandSender
     * @param cmd     Commandデータ
     * @param message メッセージ
     */
    public static void SendMessage(CommandSender sender, Command cmd, String message) {
        sender.sendMessage("[" + cmd.getName().toUpperCase() + "] " + ChatColor.GREEN + message);
    }

    public static String sdfFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 指定された期間内かどうか
     *
     * @param start 期間の開始
     * @param end   期間の終了
     * @return 期間内ならtrue、期間外ならfalse
     * @see http://www.yukun.info/blog/2009/02/java-jsp-gregoriancalendar-period.html
     */
    public static boolean isPeriod(Date start, Date end) {
        Date now = new Date();
        if (now.after(start)) {
            return now.before(end);
        }

        return false;
    }

    public static void sendAM(String str) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            String group = PermissionsManager.getPermissionMainGroup(p);
            if (!group.equalsIgnoreCase("Admin") && !group.equalsIgnoreCase("Moderator")) {
                continue;
            }
            p.sendMessage(str);
        }
    }

    public static void sendAMR(String str) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            String group = PermissionsManager.getPermissionMainGroup(p);
            if (!group.equalsIgnoreCase("Admin") && !group.equalsIgnoreCase("Moderator")
                    && !group.equalsIgnoreCase("Regular")) {
                continue;
            }
            p.sendMessage(str);
        }
    }

    public static boolean isA(Player player) {
        String group = PermissionsManager.getPermissionMainGroup(player);
        return group.equalsIgnoreCase("Admin");
    }

    public static boolean isAM(Player player) {
        String group = PermissionsManager.getPermissionMainGroup(player);
        return group.equalsIgnoreCase("Admin") || group.equalsIgnoreCase("Moderator");
    }

    public static boolean isAMR(Player player) {
        String group = PermissionsManager.getPermissionMainGroup(player);
        return group.equalsIgnoreCase("Admin") || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Regular");
    }

    public static boolean isAMRV(Player player) {
        String group = PermissionsManager.getPermissionMainGroup(player);
        return group.equalsIgnoreCase("Admin")
                || group.equalsIgnoreCase("Moderator")
                || group.equalsIgnoreCase("Regular")
                || group.equalsIgnoreCase("Verified");
    }

    public static boolean isInt(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String DiscordEscape(String text) {
        return text == null ? "" : text.replace("_", "\\_").replace("*", "\\*").replace("~", "\\~");
    }

    /**
     * CommandSenderに対してヘルプメッセージと使い方を送信します。
     *
     * @param sender
     * @param cmd
     */
    public void SendUsageMessage(CommandSender sender, Command cmd) {
        SendMessage(sender, cmd, "------- " + cmd.getName() + " --------");
        SendMessage(sender, cmd, cmd.getDescription());
        String CMDusage = cmd.getUsage();

        CMDusage = CMDusage.replaceAll("<command>", cmd.getName());

        if (CMDusage.contains("\n")) {
            String[] usages = CMDusage.split("\n");
            for (String usage : usages) {
                SendMessage(sender, cmd, usage);
            }
        } else {
            SendMessage(sender, cmd, CMDusage);
        }
    }
}
