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
import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Task.Task_ViaVerNotify;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Event_ViaVerNotify extends MyMaidLibrary implements Listener {

    @EventHandler
    public void OnEvent_ViaVerNotify(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new Task_ViaVerNotify(player).runTaskLater(Main.getJavaPlugin(), 10L);
    }
}
