package com.jaoafa.MyMaid3.Lib;

import com.jaoafa.MyMaid3.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String sdfTimeFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 指定された期間内かどうか<br></>
     * http://www.yukun.info/blog/2009/02/java-jsp-gregoriancalendar-period.html
     *
     * @param start 期間の開始
     * @param end   期間の終了
     * @return 期間内ならtrue、期間外ならfalse
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
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isUUID(String s) {
        try {
            UUID.fromString(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String DiscordEscape(String text) {
        return text == null ? "" : text.replace("_", "\\_").replace("*", "\\*").replace("~", "\\~");
    }

    /**
     * CommandSenderに対してヘルプメッセージと使い方を送信します。
     *
     * @param sender CommandSender
     * @param cmd    Cmd
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

    /**
     * 4バイトの文字列を含むかどうかを調べます
     *
     * @param str 文字列
     * @return 含むならtrue
     */
    public static boolean check4bytechars(String str) {
        Pattern pattern = Pattern.compile(".*([^\\u0000-\\uFFFF]).*");
        Matcher m = pattern.matcher(str);
        return m.matches();
    }

    /**
     * 4バイトの文字列を含むかどうかを調べ、含んでいればその文字列を返します。
     *
     * @param str 文字列
     * @return 含むならその文字列、そうでなければnull
     */
    public static String check4bytechars_MatchText(String str) {
        Pattern pattern = Pattern.compile(".*([^\\u0000-\\uFFFF]).*");
        Matcher m = pattern.matcher(str);
        if (m.matches()) {
            return m.group(1);
        }

        return null;
    }

    /**
     * 4バイトの文字列を含むかどうかを調べ、含んでいればその文字列を消したものを返します。
     *
     * @param str 文字列
     * @return 含む場合消した文字列、そうでないばあい入力された文字列
     */
    public static String check4bytechars_DeleteMatchText(String str) {
        Pattern pattern = Pattern.compile("([^\\u0000-\\uFFFF]+)");
        return pattern.matcher(str).replaceAll("");
    }

    /**
     * 指定されたLocationに一番近いプレイヤーを取得します。
     *
     * @param loc Location
     * @return 一番近いプレイヤー
     */
    public Player getNearestPlayer(Location loc) {
        double closest = Double.MAX_VALUE;
        Player closestp = null;
        for (Player i : loc.getWorld().getPlayers()) {
            double dist = i.getLocation().distance(loc);
            if (closest == Double.MAX_VALUE || dist < closest) {
                closest = dist;
                closestp = i;
            }
        }
        return closestp;
    }

    /**
     * スパムかどうかのチェックを実施し、必要に応じてJailします。
     *
     * @param player チェックを行うプレイヤー
     */
    public static void checkSpam(Player player) {
        if (MyMaidConfig.getSpamCount(player.getUniqueId()) == null || MyMaidConfig.getSpamTime(player.getUniqueId()) == null) {
            MyMaidConfig.setSpamCount(player.getUniqueId(), 1);
            MyMaidConfig.setSpamTime(player.getUniqueId(), System.currentTimeMillis());
            return;
        }
        int count = MyMaidConfig.getSpamCount(player.getUniqueId());
        long time = MyMaidConfig.getSpamTime(player.getUniqueId());

        if (System.currentTimeMillis() - time > 180000) {
            // 3分
            MyMaidConfig.setSpamCount(player.getUniqueId(), 1);
            MyMaidConfig.setSpamTime(player.getUniqueId(), System.currentTimeMillis());
            return;
        }

        if (count == 2) {
            // 今回3回目 -> Jail
            Jail jail = new Jail(player);
            if (jail.isBanned()) {
                return;
            }
            jail.addBan("jaotan", "迷惑コマンドを3分間に3回以上実行したため");
        } else if (count == 1) {
            player.sendMessage(String.format("[AntiProblemCommand] %s短時間に複数回にわたる迷惑コマンドが実行された場合、処罰対象となる場合があります。ご注意ください。", ChatColor.GREEN));
        } else {
            player.sendMessage(String.format("[AntiProblemCommand] %sあなたが実行したコマンドは迷惑コマンドとされています。複数回実行すると、迷惑行為として処罰対象となる場合がございます。", ChatColor.GREEN));
        }
        MyMaidConfig.setSpamCount(player.getUniqueId(), count + 1);
        MyMaidConfig.setSpamTime(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * フェイクのチャットを送信します。
     *
     * @param color 四角色
     * @param name  プレイヤー名
     * @param text  テキスト
     */
    public static void chatFake(ChatColor color, String name, String text) {
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + sdfTimeFormat(new Date()) + "]" + color + "■" + ChatColor.WHITE + name + ": " + text);
        MyMaidConfig.getServerChatChannel()
                .sendMessage("**" + DiscordEscape(name) + "**: " + DiscordEscape(ChatColor.stripColor(text)))
                .queue();
    }

    /**
     * 指定した地点の地面の高さを返す
     *
     * @param loc 地面を探したい場所の座標
     * @return 地面の高さ（Y座標）
     * <p>
     * http://www.jias.jp/blog/?57
     */
    public static int getGroundPos(Location loc) {

        // 最も高い位置にある非空気ブロックを取得
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

        // 最後に見つかった地上の高さ
        int ground = loc.getBlockY();

        // 下に向かって探索
        for (int y = loc.getBlockY(); y != 0; y--) {
            // 座標をセット
            loc.setY(y);

            // そこは太陽光が一定以上届く場所で、非固体ブロックで、ひとつ上も非固体ブロックか
            if (loc.getBlock().getLightFromSky() >= 8
                    && !loc.getBlock().getType().isSolid()
                    && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                // 地上の高さとして記憶しておく
                ground = y;
            }
        }

        // 地上の高さを返す
        return ground;
    }
}
