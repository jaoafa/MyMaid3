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

package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class Event_DedRain extends MyMaidLibrary implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onRainStart(WeatherChangeEvent event) {
        if (!event.isCancelled()) {
            if (event.toWeatherState() && MyMaidConfig.isDedRaining()) {
                event.setCancelled(true);
            }
        }
    }
}