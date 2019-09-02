package com.jaoafa.MyMaid3.DiscordEvent;

import org.bukkit.configuration.file.FileConfiguration;

import com.jaoafa.MyMaid3.Main;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class Event_Ready {
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("Ready: " + event.getClient().getOurUser().getName());

		Main.setDiscordClient(event.getClient());

		Main.ReportChannel = event.getClient().getChannelByID(597765357196935169L);
		FileConfiguration config = Main.getMain().getConfig();
		if (config.contains("serverchat_id")) {
			long serverchat_id = Long.valueOf(config.getString("serverchat_id"));
			Main.ServerChatChannel = event.getClient().getChannelByID(serverchat_id);
		} else {
			Main.ServerChatChannel = event.getClient().getChannelByID(597423199227084800L);
		}

	}
}
