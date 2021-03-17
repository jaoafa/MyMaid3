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

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Event_HoldSpectate extends MyMaidLibrary implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Bukkit.getServer().getOnlinePlayers().stream()
                .filter(p -> p.getSpectatorTarget() != null)
                .filter(p -> p.getSpectatorTarget().getUniqueId().equals(player.getUniqueId()))
                .forEach(p -> {
                    p.setSpectatorTarget(null);
                    p.setSpectatorTarget(player);
                });
    }
}
