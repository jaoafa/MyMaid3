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

package com.jaoafa.MyMaid3.DiscordEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Main;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class Event_Ready {
    @SubscribeEvent
    public void onReadyEvent(ReadyEvent event) {
        System.out.println("Ready: " + event.getJDA().getSelfUser().getName());

        MyMaidConfig.setJDA(event.getJDA());

        MyMaidConfig.setReportChannel(event.getJDA().getTextChannelById(597765357196935169L));
        MyMaidConfig.setJaotanChannel(event.getJDA().getTextChannelById(597423444501463040L));
        MyMaidConfig.setGeneralChannel(event.getJDA().getTextChannelById(597419057251090443L));


        FileConfiguration config = Main.getMain().getConfig();
        if (config.contains("serverchat_id")) {
            long serverchat_id = Long.parseLong(Objects.requireNonNull(config.getString("serverchat_id")));
            MyMaidConfig.setServerChatChannel(event.getJDA().getTextChannelById(serverchat_id));
        } else {
            MyMaidConfig.setServerChatChannel(event.getJDA().getTextChannelById(597423199227084800L));
        }

    }
}
