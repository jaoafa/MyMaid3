package com.jaoafa.MyMaid3.DiscordEvent;

import org.bukkit.configuration.file.FileConfiguration;

import com.jaoafa.MyMaid3.Main;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_Ready {
	@SubscribeEvent
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("Ready: " + event.getJDA().getSelfUser().getName());

		Main.setJDA(event.getJDA());

		Main.ReportChannel = event.getJDA().getTextChannelById(597765357196935169L);
		Main.jaotanChannel = event.getJDA().getTextChannelById(597423444501463040L);
		FileConfiguration config = Main.getMain().getConfig();
		if (config.contains("serverchat_id")) {
			long serverchat_id = Long.valueOf(config.getString("serverchat_id"));
			Main.ServerChatChannel = event.getJDA().getTextChannelById(serverchat_id);
		} else {
			Main.ServerChatChannel = event.getJDA().getTextChannelById(597423199227084800L);
		}

	}
}
