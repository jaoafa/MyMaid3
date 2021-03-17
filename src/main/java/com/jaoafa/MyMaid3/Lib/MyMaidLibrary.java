package com.jaoafa.MyMaid3.Lib;

import com.jaoafa.MyMaid3.Command.Cmd_TempMute;
import com.jaoafa.MyMaid3.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyMaidLibrary {
    @Nullable
    protected static JavaPlugin JavaPlugin() {
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
        sender.sendMessage(Component.text().append(
                Component.text("[" + cmd.getName().toUpperCase() + "]"),
                Component.space(),
                Component.text(message).style(Style.style(NamedTextColor.GREEN))
        ).build());
    }

    /**
     * CommandSenderに対してメッセージを送信します。
     *
     * @param sender  CommandSender
     * @param cmd     Commandデータ
     * @param message メッセージ
     */
    public static void SendMessage(CommandSender sender, Command cmd, Component component) {
        sender.sendMessage(Component.text().append(
                Component.text("[" + cmd.getName().toUpperCase() + "]"),
                Component.space(),
                component
        ).build());
    }

    /**
     * Dateをyyyy/MM/dd HH:mm:ss形式でフォーマットします。
     *
     * @param date フォーマットするDate
     * @return フォーマットされた結果文字列
     */
    public static String sdfFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date);
    }


    /**
     * DateをHH:mm:ss形式でフォーマットします。
     *
     * @param date フォーマットするDate
     * @return フォーマットされた結果文字列
     */
    private static String sdfTimeFormat(Date date) {
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
        if (now.after(start)) return now.before(end);

        return false;
    }

    /**
     * Admin・Moderatorにメッセージを送信します。
     *
     * @param str 送信するメッセージ文字列
     */
    public static void sendAM(String str) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (Cmd_TempMute.tempmutes.stream().anyMatch(pf -> pf.getUniqueId().equals(p.getUniqueId()))) continue;
            String group = PermissionsManager.getPermissionMainGroup(p);
            if (!isAM(p)) continue;
            p.sendMessage(str);
        }
    }

    /**
     * Admin・Moderator・Regularにメッセージを送信します。
     *
     * @param str 送信するメッセージ文字列
     */
    public static void sendAMR(String str) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (Cmd_TempMute.tempmutes.stream().anyMatch(pf -> pf.getUniqueId().equals(p.getUniqueId()))) continue;
            String group = PermissionsManager.getPermissionMainGroup(p);
            if (!isAMR(p)) continue;
            p.sendMessage(str);
        }
    }

    /**
     * Admin・Moderator・Regular・Verifiedにメッセージを送信します。
     *
     * @param str 送信するメッセージ文字列
     */
    public static void sendAMRV(String str) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (Cmd_TempMute.tempmutes.stream().anyMatch(pf -> pf.getUniqueId().equals(p.getUniqueId()))) continue;
            String group = PermissionsManager.getPermissionMainGroup(p);
            if (!isAMRV(p)) continue;
            p.sendMessage(str);
        }
    }

    /**
     * プレイヤーがAdminであるかを判定します。
     *
     * @param player 判定するプレイヤー
     */
    protected static boolean isA(Player player) {
        String group = PermissionsManager.getPermissionMainGroup(player);
        return group.equalsIgnoreCase("Admin");
    }

    /**
     * プレイヤーがAdmin・Moderatorのいずれかであるかを判定します。
     *
     * @param player 判定するプレイヤー
     */
    public static boolean isAM(Player player) {
        String group = PermissionsManager.getPermissionMainGroup(player);
        return isA(player) || group.equalsIgnoreCase("Moderator");
    }

    /**
     * プレイヤーがAdmin・Moderator・Regularのいずれかであるかを判定します。
     *
     * @param player 判定するプレイヤー
     */
    public static boolean isAMR(Player player) {
        String group = PermissionsManager.getPermissionMainGroup(player);
        return isAM(player) || group.equalsIgnoreCase("Regular");
    }

    /**
     * プレイヤーがAdmin・Moderator・Verifiedのいずれかであるかを判定します。
     *
     * @param player 判定するプレイヤー
     */
    protected static boolean isAMRV(Player player) {
        String group = PermissionsManager.getPermissionMainGroup(player);
        return isAMR(player) || group.equalsIgnoreCase("Verified");
    }

    /**
     * 文字列が数値であるかを判定します。
     *
     * @param s 判定する文字列
     * @return 判定結果
     */
    protected static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 文字列がUUIDとして正しいか判定します。
     *
     * @param s 判定する文字列
     * @return 判定結果
     */
    protected static boolean isUUID(String s) {
        return s.split("-").length == 5;
    }

    /**
     * 文字列をDiscord用にエスケープします。
     *
     * @param text エスケープする文字列
     * @return エスケープされた文字列
     */
    protected static String DiscordEscape(String text) {
        return text == null ? "" : text.replace("_", "\\_").replace("*", "\\*").replace("~", "\\~");
    }

    /**
     * CommandSenderに対してヘルプメッセージと使い方を送信します。
     *
     * @param sender      CommandSender
     * @param description getDescription()
     * @param cmdUsage    getUsage()
     */
    public static void SendUsageMessage(CommandSender sender, String description, CmdUsage cmdUsage) {
        sender.sendMessage("------- " + cmdUsage.getCommand() + " --------");
        sender.sendMessage(description);

        for (CmdUsage.Cmd cmd : cmdUsage.getCommands()){
            sender.sendMessage(Component.text().append(
                    Component.text("・"),
                    Component.text(String.format("/%s %s", cmdUsage.getCommand(), cmd.getArgs()))
                        .clickEvent(ClickEvent.suggestCommand(String.format("/%s %s", cmdUsage.getCommand(), cmd.getArgs())))
                        .hoverEvent(HoverEvent.showText(Component.text("コマンドをサジェストします"))),
                    Component.text(":"),
                    Component.space(),
                    Component.text(cmd.getDetails())
            ));
        }
    }

    /**
     * 4バイトの文字列を含むかどうかを調べます
     *
     * @param str 文字列
     * @return 含むならtrue
     */
    protected static boolean check4bytechars(String str) {
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
    protected static String check4bytechars_MatchText(String str) {
        Pattern pattern = Pattern.compile(".*([^\\u0000-\\uFFFF]).*");
        Matcher m = pattern.matcher(str);
        if (m.matches()) return m.group(1);

        return null;
    }

    /**
     * 4バイトの文字列を含むかどうかを調べ、含んでいればその文字列を消したものを返します。
     *
     * @param str 文字列
     * @return 含む場合消した文字列、そうでない場合入力された文字列
     */
    protected static String check4bytechars_DeleteMatchText(String str) {
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
            if (jail.isBanned()) return;
            jail.addBan("jaotan", "迷惑コマンドを3分間に3回以上実行したため");
        } else if (count == 1)
            player.sendMessage(String.format("[AntiProblemCommand] %s短時間に複数回にわたる迷惑コマンドが実行された場合、処罰対象となる場合があります。ご注意ください。", ChatColor.GREEN));
        else
            player.sendMessage(String.format("[AntiProblemCommand] %sあなたが実行したコマンドは迷惑コマンドとされています。複数回実行すると、迷惑行為として処罰対象となる場合がございます。", ChatColor.GREEN));
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
    protected static int getGroundPos(Location loc) {

        // 最も高い位置にある非空気ブロックを取得
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

        // 最後に見つかった地上の高さ
        int ground = loc.getBlockY();

        // 下に向かって探索
        for (int y = loc.getBlockY(); y != 0; y--) {
            // 座標をセット
            loc.setY(y);

            // そこは太陽光が一定以上届く場所で、非固体ブロックで、ひとつ上も非固体ブロックか
            // 地上の高さとして記憶しておく
            if (loc.getBlock().getLightFromSky() >= 8
                    && !loc.getBlock().getType().isSolid()
                    && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) ground = y;
        }

        // 地上の高さを返す
        return ground;
    }
}
