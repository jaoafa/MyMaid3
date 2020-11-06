package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class Event_AntiNetherPortal extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onEntityCreatePortalEvent(EntityCreatePortalEvent event) {
        event.setCancelled(true);
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (isA(player)) {
            // あどみん
            player.sendMessage("[" + ChatColor.RED + "NoNetherPortal" + ChatColor.WHITE + "] " + ChatColor.GREEN + "ネザーポータルの生成は基本的に管理部以外禁止されています。必要のない場合は基本的に破壊してください。");
            event.setCancelled(false);
            return;
        }
        player.sendMessage("[" + ChatColor.RED + "NoNetherPortal" + ChatColor.WHITE + "] " + ChatColor.GREEN + "負荷対策および不必要な破壊を抑制するため、ネザーポータルの生成を禁止しています。ご協力をお願いします。");
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (isAM(p)) {
                p.sendMessage("[" + ChatColor.RED + "NoNetherPortal" + ChatColor.WHITE + "] " + ChatColor.GREEN + player.getName() + "の近くでネザーポータルの生成がされましたが、生成を規制されました。");
            }
        }
    }

    @EventHandler
    public void onPortalCreateEvent(PortalCreateEvent event) {
        if (event.getBlocks().size() <= 0) {
            event.setCancelled(true);
            return;
        }
        Location location = event.getBlocks().get(0).getLocation();
        double min = 1.79769313486231570E+308;
        Player min_player = null;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            org.bukkit.Location location_p = player.getLocation();
            if (location.getWorld().getName().equals(location_p.getWorld().getName())) {
                double distance = location.distance(location_p);
                if (distance < min) {
                    min = distance;
                    min_player = player;
                }
            }
        }
        if (min_player == null) {
            event.setCancelled(true);
            return;
        }
        if (isA(min_player)) {
            // あどみん
            min_player.sendMessage("[" + ChatColor.RED + "NoNetherPortal" + ChatColor.WHITE + "] " + ChatColor.GREEN + "ネザーポータルの生成は基本的に管理部以外禁止されています。必要のない場合は基本的に破壊してください。");
            return;
        }
        event.setCancelled(true);
        min_player.sendMessage("[" + ChatColor.RED + "NoNetherPortal" + ChatColor.WHITE + "] " + ChatColor.GREEN + "負荷対策および不必要な破壊を抑制するため、ネザーポータルの生成を禁止しています。ご協力をお願いします。");
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (isAM(p)) {
                p.sendMessage("[" + ChatColor.RED + "NoNetherPortal" + ChatColor.WHITE + "] " + ChatColor.GREEN + min_player.getName() + "の近くでネザーポータルの生成がされましたが、生成を規制されました。");
            }
        }
    }
}
