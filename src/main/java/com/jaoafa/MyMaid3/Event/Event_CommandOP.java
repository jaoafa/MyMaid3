package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.logging.Logger;

public class Event_CommandOP extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onOPCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        String[] args = command.split(" ", 0);
        if (args.length == 0) {
            return; // 本来発生しないと思うけど
        }
        if (!args[0].equalsIgnoreCase("/op")
                && !args[0].equalsIgnoreCase("/minecraft:op")) {
            return; // opコマンド以外
        }

        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This command is not allowed to be run from within the game. This incident will be reported.");
        event.setCancelled(true);
    }
}
