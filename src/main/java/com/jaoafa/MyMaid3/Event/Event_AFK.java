package com.jaoafa.MyMaid3.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.connorlinfoot.titleapi.TitleAPI;
import com.jaoafa.MyMaid3.Lib.AFKPlayer;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Event_AFK extends MyMaidLibrary implements Listener {
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		AFKPlayer afkplayer = new AFKPlayer(player);
		afkplayer.setNowLastActionTime();
		if (!afkplayer.isAFK()) {
			return;
		}
		afkplayer.end();
	}

	@EventHandler
	public void OnEvent_PlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		AFKPlayer afkplayer = new AFKPlayer(player);
		afkplayer.clear();
	}

	@EventHandler
	public void OnEvent_PlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		AFKPlayer afkplayer = new AFKPlayer(player);
		afkplayer.setNowLastActionTime();
		if (!afkplayer.isAFK()) {
			return;
		}
		afkplayer.end();
		TitleAPI.clearTitle(player);
	}
}
