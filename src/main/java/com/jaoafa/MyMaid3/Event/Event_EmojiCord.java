package com.jaoafa.MyMaid3.Event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Event_EmojiCord extends MyMaidLibrary implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		Pattern p = Pattern.compile("<:(.+?):(\\w+?)=>");
		Matcher m = p.matcher(message);
		if (!m.find()) {
			return;
		}
		Player player = event.getPlayer();
		// とりあえずめんどいので:TEXT:形式に直す
		player.chat(m.group(1));
		event.setCancelled(true);
	}
}