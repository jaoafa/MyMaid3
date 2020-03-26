package com.jaoafa.MyMaid3.Event;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Event_SpectatorHide extends MyMaidLibrary implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameModeChange(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		GameMode mode = event.getNewGameMode();

		if (mode == GameMode.SPECTATOR) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dynmap hide " + player.getName());
		} else if (mode == GameMode.SPECTATOR) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dynmap show " + player.getName());
		}
	}
}
