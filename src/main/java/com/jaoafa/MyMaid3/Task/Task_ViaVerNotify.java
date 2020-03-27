package com.jaoafa.MyMaid3.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;

import com.jaoafa.MyMaid3.Lib.PermissionsManager;

import us.myles.ViaVersion.api.Via;

public class Task_ViaVerNotify extends BukkitRunnable {
	Player player;

	public Task_ViaVerNotify(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		if (!player.isOnline()) {
			return;
		}
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
			sendAMR("[ViaVersion] " + ChatColor.AQUA + player.getName() + " -> (" + ver + ")");
			return;
		}
		String version = obj.getString(verstr);

		if (!version.equals("1.12.2")) {
			player.sendMessage("[VersionChecker] " + ChatColor.AQUA + "あなたはクライアントバージョン「" + version + "」で接続しています。");
			player.sendMessage("[VersionChecker] " + ChatColor.AQUA + "サーババージョンは1.12.2のため、" + ChatColor.RED
					+ "一部のブロック・機能は利用できません。");
			player.sendMessage("[VersionChecker] " + ChatColor.AQUA + "クライアントバージョンを「1.12.2」にすることを強くお勧めします。");
		}

		sendAMR("[ViaVersion] " + ChatColor.AQUA + player.getName() + " -> " + version + " (" + ver + ")");
	}

	String toString(InputStream is) throws IOException {
		InputStreamReader reader = new InputStreamReader(is);
		StringBuilder builder = new StringBuilder();
		char[] buf = new char[1024];
		int numRead;
		while (0 <= (numRead = reader.read(buf))) {
			builder.append(buf, 0, numRead);
		}
		return builder.toString();
	}

	void sendAMR(String str) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			String group = PermissionsManager.getPermissionMainGroup(p);
			if (!group.equalsIgnoreCase("Admin") && !group.equalsIgnoreCase("Moderator")
					&& !group.equalsIgnoreCase("Regular")) {
				continue;
			}
			p.sendMessage(str);
		}
	}
}
