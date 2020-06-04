package com.jaoafa.MyMaid3.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

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
		SendMessage(player, "CmdLengthLimiter", "あなたの権限では@rセレクターの使用はできません。");
	}
}
