package com.jaoafa.MyMaid3.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.TeleportAlias;
import com.jaoafa.MyMaid3.Task.Task_TeleportAlias;

public class Event_TeleportAlias extends MyMaidLibrary implements Listener {
	@EventHandler
	public void onEvent_AntiProblemCommand(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage();
		Player player = event.getPlayer();
		if (!command.contains(" ")) {
			return;
		}
		String[] args = command.split(" ", 0);
		if (!args[0].equalsIgnoreCase("/tp")) {
			return;
		}
		if (args.length == 2) { // /tp <Player>
			String to = args[1];
			String replacement = TeleportAlias.getReplaceAlias(to);
			if (replacement == null) {
				return;
			}
			new Task_TeleportAlias(player, replacement).runTaskLater(Main.getJavaPlugin(), 5L);
			event.setCancelled(true);
			return;
		}
	}
}
