package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.ArrayList;
import java.util.List;

public class Event_Anti4BYTES extends MyMaidLibrary implements Listener {
    List<String> sendedCmdb = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!check4bytechars(message)) {
            return;
        }
        player.sendMessage("[4BYTESChecker] " + ChatColor.GREEN
                + "メッセージ内に絵文字などの4バイト文字が含まれています。Minecraftの仕様上、4バイト文字は表示されません。注意してください。");

        TextChannel channel = MyMaidConfig.getJDA().getTextChannelById(617805813553299456L);
        if (channel == null) {
            Main.getJavaPlugin().getLogger().info(String.format("[4BYTESChecker] channel = null | %s / message: %s / matchText: %s", player.getName(), message, check4bytechars_MatchText(message)));
            return;
        }
        channel.sendMessage(String.format("プレイヤー「%s」が投稿したメッセージ内に4バイト文字が含まれていました。\nメッセージ: ```%s```\n判定された対象文字列: ```%s```", player.getName(), message, check4bytechars_MatchText(message))).queue(
                null,
                failure -> Main.getJavaPlugin().getLogger().info(String.format("[4BYTESChecker] Exception: %s | %s / message: %s / matchText: %s", failure.getMessage(), player.getName(), message, check4bytechars_MatchText(message)))
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!check4bytechars(message)) {
            return;
        }
        player.sendMessage("[4BYTESChecker] " + ChatColor.GREEN
                + "コマンド内に絵文字などの4バイト文字が含まれています。Minecraftの仕様上、4バイト文字は表示されません。注意してください。");
        player.sendMessage("[4BYTESChecker] " + ChatColor.GREEN
                + "また、アイテム名などに4バイト文字列を含まれているとチャンク破損等を起こす可能性があります。出来る限り使用は避けてください。");

        TextChannel channel = MyMaidConfig.getJDA().getTextChannelById(617805813553299456L);
        if (channel == null) {
            Main.getJavaPlugin().getLogger().info(String.format("[4BYTESChecker] channel = null | %s / message: %s / matchText: %s", player.getName(), message, check4bytechars_MatchText(message)));
            return;
        }
        channel.sendMessage(String.format("プレイヤー「%s」が実行したコマンド内に4バイト文字が含まれていました。\nメッセージ: ```%s```\n判定された対象文字列: ```%s```", player.getName(), message, check4bytechars_MatchText(message))).queue(
                null,
                failure -> Main.getJavaPlugin().getLogger().info(String.format("[4BYTESChecker] Exception: %s | %s / message: %s / matchText: %s", failure.getMessage(), player.getName(), message, check4bytechars_MatchText(message)))
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        String[] messages = event.getLines();

        int i = -1;
        for (String message : messages) {
            i++;
            if (!check4bytechars(message)) {
                continue;
            }
            //player.sendMessage("[4BYTESChecker] " + ChatColor.GREEN
            //        + "看板内に絵文字などの4バイト文字が含まれています。Minecraftの仕様上、4バイト文字は表示されません。注意してください。");
            //player.sendMessage("[4BYTESChecker] " + ChatColor.GREEN
            //        + "また、アイテム名などに4バイト文字列を含まれているとチャンク破損等を起こす可能性があります。出来る限り使用は避けてください。");

            player.sendMessage("[4BYTESChecker] " + ChatColor.GREEN
                    + "看板内に絵文字などの4バイト文字が含まれています。チャンクに影響を及ぼす可能性があるため、該当文字を削除します。");

            event.setLine(i, check4bytechars_DeleteMatchText(message));

            TextChannel channel = MyMaidConfig.getJDA().getTextChannelById(617805813553299456L);
            if (channel == null) {
                Main.getJavaPlugin().getLogger().info(String.format("[4BYTESChecker] channel = null | %s / %s %d %d %d / message: %s / matchText: %s", player.getName(), world, x, y, z, message, check4bytechars_MatchText(message)));
                return;
            }
            channel.sendMessage(String.format("プレイヤー「%s」が設置した看板(%s %d %d %d)内に4バイト文字が含まれていたため、該当文字を削除しました。\nコマンド: ```%s```\n判定された対象文字列: ```%s```", player.getName(), world, x, y, z, message, check4bytechars_MatchText(message))).queue(
                    null,
                    failure -> Main.getJavaPlugin().getLogger().info(String.format("[4BYTESChecker] Exception: %s | %s / %s %d %d %d / message: %s / matchText: %s", failure.getMessage(), player.getName(), world, x, y, z, message, check4bytechars_MatchText(message)))
            );
        }
    }

    @EventHandler
    public void onCommandBlockCall(ServerCommandEvent event) {
        if (!(event.getSender() instanceof BlockCommandSender))
            return;
        BlockCommandSender sender = (BlockCommandSender) event.getSender();

        if (sender.getBlock() == null || !(sender.getBlock().getState() instanceof CommandBlock))
            return;
        CommandBlock cmdb = (CommandBlock) sender.getBlock().getState();

        String command = cmdb.getCommand();

        String world = cmdb.getWorld().getName();
        int x = cmdb.getX();
        int y = cmdb.getY();
        int z = cmdb.getZ();

        if (!check4bytechars(command)) {
            return;
        }

        if (sendedCmdb.contains(command)) {
            return;
        }

        Player nearestPlayer = getNearestPlayer(cmdb.getLocation());
        if (nearestPlayer == null) {
            return;
        }
        nearestPlayer.sendMessage("[4BYTESChecker] " + ChatColor.GREEN + "あなたの近くで実行されたコマンドブロック(" + world + " " + x + " "
                + y + " " + z + ")のコマンド内に絵文字などの4バイト文字が含まれています。Minecraftの仕様上、4バイト文字は表示されません。注意してください。");
        nearestPlayer.sendMessage("[4BYTESChecker] " + ChatColor.GREEN
                + "また、アイテム名やコマンドなどに4バイト文字列を含まれているとチャンク破損等を起こす可能性があります。出来る限り使用は避けてください。");

        TextChannel channel = MyMaidConfig.getJDA().getTextChannelById(617805813553299456L);
        if (channel == null) {
            Main.getJavaPlugin().getLogger().info(String.format("[4BYTESChecker] channel = null | %s / %s %d %d %d / message: %s / matchText: %s", nearestPlayer.getName(), world, x, y, z, command, check4bytechars_MatchText(command)));
            return;
        }
        channel.sendMessage(String.format("プレイヤー「%s」の近くで実行されたコマンドブロック(%s %d %d %d)のコマンド内に4バイト文字が含まれていました。\nコマンド: ```%s```\n判定された対象文字列: ```%s```", nearestPlayer.getName(), world, x, y, z, command, check4bytechars_MatchText(command))).queue(
                null,
                failure -> Main.getJavaPlugin().getLogger().info(String.format("[4BYTESChecker] Exception: %s | %s / %s %d %d %d / message: %s / matchText: %s", failure.getMessage(), nearestPlayer.getName(), world, x, y, z, command, check4bytechars_MatchText(command)))
        );

        sendedCmdb.add(command);
    }
}
