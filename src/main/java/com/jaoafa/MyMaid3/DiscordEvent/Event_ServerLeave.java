package com.jaoafa.MyMaid3.DiscordEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.ErrorReporter;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class Event_ServerLeave {
	@EventSubscriber
	public void onMemberLeaveEvent(UserLeaveEvent event) {
		if (event.getGuild().getLongID() != 597378876556967936L) {
			return; // jMS Gamers Clubのみ
		}
		IUser user = event.getUser();
		IChannel channel = event.getGuild().getChannelByID(597423444501463040L);
		String player = null;
		String uuid = null;
		try {
			Connection conn = Main.getMySQLDBManager().getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM discordlink WHERE disid = ? AND disabled = ?");
			statement.setString(1, user.getStringID());
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
			PermissionsManager.setPermissionsGroup(uuid, "Default");

			try {
				Connection conn = Main.getMySQLDBManager().getConnection();
				PreparedStatement statement = conn
						.prepareStatement("UPDATE discordlink SET disabled = ? WHERE disid = ?");
				statement.setInt(1, 1);
				statement.setString(2, user.getStringID());
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				ErrorReporter.report(e);
				return;
			}

			String _player = player;
			RequestBuffer.request(() -> {
				try {
					channel.sendMessage(
							":wave:" + user.getName() + "#" + user.getDiscriminator() + "が退出したため、" + _player
									+ "の連携が無効化され、Defaultに降格しました。");
				} catch (DiscordException discordexception) {
					Main.DiscordExceptionError(getClass(), channel, discordexception);
				}
			});
		}
	}
}
