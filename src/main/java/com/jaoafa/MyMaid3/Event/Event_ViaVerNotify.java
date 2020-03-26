package com.jaoafa.MyMaid3.Event;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONException;
import org.json.JSONObject;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

import us.myles.ViaVersion.api.Via;

public class Event_ViaVerNotify extends MyMaidLibrary implements Listener {
	@EventHandler
	public void OnEvent_FirstLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (Bukkit.getPluginManager().getPlugin("ViaVersion") == null) {
			return;
		}
		int ver = Via.getAPI().getPlayerVersion(player.getUniqueId());
		String verstr = Integer.toString(ver);
		InputStream is = getClass().getResourceAsStream("/versions.json");
		JSONObject obj;
		try {
			String str = toString(is);
			obj = new JSONObject(str);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return;
		}
		if (!obj.has(verstr)) {
			// version not found.
			sendAMR("[ViaVersion] " + ChatColor.YELLOW + player.getName() + " -> (" + ver + ")");
			return;
		}
		String version = obj.getString(verstr);

		sendAMR("[ViaVersion] " + ChatColor.YELLOW + player.getName() + " -> " + version + " (" + ver + ")");
	}

	static String toString(InputStream is) throws IOException {
		InputStreamReader reader = new InputStreamReader(is);
		StringBuilder builder = new StringBuilder();
		char[] buf = new char[1024];
		int numRead;
		while (0 <= (numRead = reader.read(buf))) {
			builder.append(buf, 0, numRead);
		}
		return builder.toString();
	}
}
