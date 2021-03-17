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

import com.jaoafa.MyMaid3.Lib.EBan;
import com.jaoafa.MyMaid3.Lib.Jail;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

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

        Jail jail = new Jail(player);
        if (jail.isBanned()) {
            event.setCancelled(true);
            return;
        }
        EBan eban = new EBan(player);
        if (eban.isBanned()) {
            event.setCancelled(true);
            return;
        }

        Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " tried to execute the following command: " + command);
        MyMaidConfig.getJaotanChannel().sendMessage("**" + player.getName() + " tried to execute the following command: " + command + "**").queue();
        event.setCancelled(true);
    }
}
