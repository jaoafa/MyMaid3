package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class Event_DedRain extends MyMaidLibrary implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onRainStart(WeatherChangeEvent event) {
		if (!event.isCancelled()) {
			if (event.toWeatherState() && MyMaidConfig.isDedRaining()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		world.setThundering(false);
		world.setStorm(false);
	}
}