package com.jaoafa.MyMaid3.Event;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.ErrorReporter;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class Event_PlayerCheckPreLogin extends MyMaidLibrary implements Listener {
	@EventHandler
	public void OnEvent_PlayerCheckPreLogin(AsyncPlayerPreLoginEvent event) {
		String name = event.getName();
		UUID uuid = event.getUniqueId();
		InetAddress ia = event.getAddress();
		String ip = ia.getHostAddress();
		String host = ia.getHostName();

		Country country = null;
		String countryName = null;
		City city = null;
		String cityName = null;
		if (!(ia.isAnyLocalAddress() || ia.isLoopbackAddress()) && !ip.startsWith("192.168")) {
			CityResponse res = getGeoIP(ia);
			if (res != null) {
				country = res.getCountry();
				countryName = country.getName();
				city = res.getCity();
				cityName = city.getName();
			}
		}

		String permission = getPermissionGroup(uuid);
		if (country != null && city != null) {
			Main.getJavaPlugin().getLogger().info("Country: " + country.getName() + " (" + country.getIsoCode() + ")");
			Main.getJavaPlugin().getLogger().info("City: " + city.getName() + " (" + city.getGeoNameId() + ")");
		}
		Main.getJavaPlugin().getLogger().info("Permission: " + permission);

		// 「jaotan」というプレイヤー名は禁止
		if (name.equalsIgnoreCase("jaotan")) {
			disallow(event,
					ChatColor.WHITE + "あなたのMinecraftIDは、システムの運用上の問題によりログイン不可能と判断されました。\n"
							+ ChatColor.RESET + ChatColor.AQUA + "ログインするには、MinecraftIDを変更してください。",
					"UserName");
			return;
		}

		// 日本国外からのアクセスをすべて規制
		if (country != null && !countryName.equalsIgnoreCase("Japan")) {
			disallow(event,
					ChatColor.WHITE + "海外からのログインと判定されました。\n"
							+ ChatColor.RESET + ChatColor.AQUA + "当サーバでは、日本国外からのログインを禁止しています。",
					"Region restricted",
					countryName + " " + cityName);
			return;
		}

		final String finalCountry = countryName;
		final String finalCity = cityName;
		new BukkitRunnable() {
			public void run() {
				MySQLDBManager MySQLDBManager = MyMaidConfig.getMySQLDBManager();
				if (MySQLDBManager == null) {
					return;
				}
				try {
					Connection conn = MySQLDBManager.getConnection();
					PreparedStatement statement = conn.prepareStatement(
							"INSERT INTO login (player, uuid, ip, host, countryName, city, permission) VALUES (?, ?, ?, ?, ?, ?, ?);");
					statement.setString(1, name); // player
					statement.setString(2, uuid.toString()); // uuid
					statement.setString(3, ip); // ip
					statement.setString(4, host); // host
					statement.setString(5, finalCountry); // countryName
					statement.setString(6, finalCity); // city
					statement.setString(7, permission); // permission
					statement.executeUpdate();
					statement.close();
				} catch (SQLException e) {
					ErrorReporter.report(e);
					return;
				}
			}
		}.runTaskAsynchronously(Main.getJavaPlugin());
	}

	CityResponse getGeoIP(InetAddress ia) {
		JavaPlugin plugin = Main.getJavaPlugin();
		File file = new File(plugin.getDataFolder(), "GeoLite2-City.mmdb");
		if (!file.exists()) {
			plugin.getLogger().warning("GeoLite2-City.mmdb not found. Check Login failed.");
			return null;
		}

		try {
			DatabaseReader dr = new DatabaseReader.Builder(file).build();
			CityResponse res = dr.city(ia);
			return res;
		} catch (IOException e) {
			plugin.getLogger().warning("IOException catched. getGeoIP failed.");
			e.printStackTrace();
			return null;
		} catch (GeoIp2Exception e) {
			plugin.getLogger().warning("GeoIp2Exception catched. getGeoIP failed.");
			e.printStackTrace();
			return null;
		}
	}

	String getPermissionGroup(UUID uuid) {
		LuckPerms LPApi = LuckPermsProvider.get();
		User LPplayer = LPApi.getUserManager().getUser(uuid);
		if (LPplayer == null) {
			return null;
		}
		String groupname = LPplayer.getPrimaryGroup();
		return groupname;
	}

	private void disallow(AsyncPlayerPreLoginEvent event, String message, String reason) {
		event.disallow(Result.KICK_FULL,
				ChatColor.RED + "[Login Denied! - Reason: " + reason + "]\n"
						+ ChatColor.RESET + message
						+ ChatColor.RESET + ChatColor.WHITE + "もしこの判定が誤判定と思われる場合は、公式Discordへお問い合わせください。");
		MyMaidConfig.getJaotanChannel().sendMessage(
				"[MyMaid3-PreLoginCheck] " + event.getName() + " -> `" + reason + "`").queue();
	}

	private void disallow(AsyncPlayerPreLoginEvent event, String message, String reason, String data) {
		event.disallow(Result.KICK_FULL,
				ChatColor.RED + "[Login Denied! - Reason: " + reason + "]\n"
						+ ChatColor.RESET + message + "\n"
						+ ChatColor.RESET + ChatColor.WHITE + "もしこの判定が誤判定と思われる場合は、公式Discordへお問い合わせください。");
		MyMaidConfig.getJaotanChannel().sendMessage(
				"[MyMaid3-PreLoginCheck] " + event.getName() + " -> `" + reason + " (" + data + ")`").queue();
	}
}
