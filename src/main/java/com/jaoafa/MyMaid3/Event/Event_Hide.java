/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Event_Hide extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage();
        if (!MyMaidConfig.isHid(player.getUniqueId())) {
            return;
        }
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!isAM(p)) {
                continue;
            }
            p.sendMessage(ChatColor.GRAY + player.getName() + " > " + msg);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (!MyMaidConfig.isHid(p.getUniqueId())) {
                player.showPlayer(Main.getJavaPlugin(), p);
                continue;
            }
            player.hidePlayer(Main.getJavaPlugin(), p);

            p.sendMessage("[Hide] " + ChatColor.RED + "プレイヤー「" + player.getName() + "」にあなたのhideモードを反映しました。");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!MyMaidConfig.isHid(p.getUniqueId())) {
                continue;
            }
            if (!command.toLowerCase().contains(p.getName().toLowerCase())) {
                continue;
            }
            player.sendMessage("Entity '%s' cannot be found".replace("%s", p.getName()));
            event.setCancelled(true);
            return;
        }
    }
}
