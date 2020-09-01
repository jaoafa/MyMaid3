package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Event_AntiProblemTeleport extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        Player player = event.getPlayer();
        if (!from.getWorld().getName().equals(to.getWorld().getName())) {
            return;
        }
        if (from.distance(to) >= 10000) {
            player.sendMessage("[TeleportCheck] " + ChatColor.GREEN + "テレポートに失敗しました。");
            event.setCancelled(true);
        }
    }
}
