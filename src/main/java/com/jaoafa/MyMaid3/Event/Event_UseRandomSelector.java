package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Event_UseRandomSelector extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();

        if (isAMR(player)) {
            // Default, Verified以外
            return;
        }

        if (!command.contains("@r")) {
            return;
        }

        event.setCancelled(true);
        SendMessage(player, "UseRandomSelector", "あなたの権限では@rセレクターの使用はできません。");
    }
}
