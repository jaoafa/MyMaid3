package com.jaoafa.MyMaid3.DiscordEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.ErrorReporter;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_ServerLeave {
	@SubscribeEvent
	public void onMemberLeaveEvent(GuildMemberRemoveEvent event) {
		if (event.getGuild().getIdLong() != 597378876556967936L) {
			return; // jMS Gamers Clubのみ
		}
		User user = event.getUser();
		TextChannel channel = event.getGuild().getTextChannelById(597423444501463040L);
		String player = null;
		String uuid = null;
		try {
			Connection conn = Main.getMySQLDBManager().getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM discordlink WHERE disid = ? AND disabled = ?");
			statement.setString(1, user.getId());
			statement.setInt(2, 0);
			ResultSet res = statement.executeQuery();
			if (res.next()) {
				// ある
				player = res.getString("player");
				uuid = res.getString("uuid");
			}
			statement.close();
		} catch (SQLException e) {
			ErrorReporter.report(e);
			return;
		}
		if (player != null) {
			PermissionsManager.setPermissionsGroup(uuid, "default");

			try {
				Connection conn = Main.getMySQLDBManager().getConnection();
				PreparedStatement statement = conn
						.prepareStatement("UPDATE discordlink SET disabled = ? WHERE disid = ?");
				statement.setInt(1, 1);
				statement.setString(2, user.getId());
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				ErrorReporter.report(e);
				return;
			}

			channel.sendMessage(":wave:" + user.getName() + "#" + user.getDiscriminator() + "が退出したため、" + player
					+ "の連携が無効化され、Defaultに降格しました。").queue();
		}
	}
}
