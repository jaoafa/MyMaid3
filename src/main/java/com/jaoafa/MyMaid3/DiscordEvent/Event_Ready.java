package com.jaoafa.MyMaid3.DiscordEvent;

import org.bukkit.configuration.file.FileConfiguration;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_Ready {
	@SubscribeEvent
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("Ready: " + event.getJDA().getSelfUser().getName());

		MyMaidConfig.setJDA(event.getJDA());

		MyMaidConfig.setReportChannel(event.getJDA().getTextChannelById(597765357196935169L));
		MyMaidConfig.setJaotanChannel(event.getJDA().getTextChannelById(597423444501463040L));
		FileConfiguration config = Main.getMain().getConfig();
		if (config.contains("serverchat_id")) {
			long serverchat_id = Long.valueOf(config.getString("serverchat_id"));
			MyMaidConfig.setServerChatChannel(event.getJDA().getTextChannelById(serverchat_id));
		} else {
			MyMaidConfig.setServerChatChannel(event.getJDA().getTextChannelById(597423199227084800L));
		}

	}
}
