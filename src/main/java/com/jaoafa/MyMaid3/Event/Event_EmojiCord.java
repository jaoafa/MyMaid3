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
		Pattern p = Pattern
				.compile("<a?\\:(\\w+?)\\:([a-zA-Z0-9+/=]+?)>|\\:([\\w+-]+?(?:~\\d+?)?)\\:(?:\\:skin-tone-(\\d)\\:)?");
		Matcher m = p.matcher(message);
		boolean match = false;
		while (m.find()) {
			if (m.group(1) == null || m.group(1).equals("")) {
				continue;
			}
			System.out.println("[ECREPLACE] " + m.group() + " -> " + m.group(1));
			message = message.replace(m.group(), m.group(1));
			match = true;
		}
		Player player = event.getPlayer();
		// とりあえずめんどいので:TEXT:形式に直す
		if (match) {
			player.chat(message);
			event.setCancelled(true);
		} else {
			String new_message = message.replaceAll(
					"<a?\\:(\\w+?)\\:([a-zA-Z0-9+/=]+?)>|\\:([\\w+-]+?(?:~\\d+?)?)\\:(?:\\:skin-tone-(\\d)\\:)?",
					":$1:");
			if (!message.equals(new_message)) {
				player.chat(new_message);
				event.setCancelled(true);
			}
		}
	}
}
