package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.ChatBan;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Event_ChatJail extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnEvent_LoginChatJailCheck(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ChatBan chatjail = new ChatBan(player);
        if (!chatjail.isBanned()) {
            return;
        }
        String reason = chatjail.getLastBanReason();
        if (reason == null) {
            return;
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!MyMaidLibrary.isAMR(p)) {
                return;
            }
            p.sendMessage("[ChatBan] " + ChatColor.RED + "プレイヤー「" + player.getName() + "」は、「" + reason + "」という理由でChatJailされています。");
            p.sendMessage("[ChatBan] " + ChatColor.RED + "詳しい情報はユーザーページよりご確認ください。");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ChatBan chatjail = new ChatBan(player);
        if (!chatjail.isBanned()) {
            return;
        }
        String reason = chatjail.getLastBanReason();
        if (reason == null) {
            reason = "不明";
        }
        String message = event.getMessage();
        player.sendMessage("[ChatBan] " + ChatColor.RED + "あなたは、「" + reason + "」という理由でチャット規制をされています。");
        player.sendMessage("[ChatBan] " + ChatColor.RED + "解除申請の方法や、Banの方針などは以下ページをご覧ください。");
        player.sendMessage("[ChatBan] " + ChatColor.RED + "https://jaoafa.com/rule/management/ban");
        chatjail.addMessageDB(message);
        event.setCancelled(true);
    }
}
