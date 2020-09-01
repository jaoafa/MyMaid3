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
