package com.jaoafa.MyMaid3.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.ErrorReporter;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class Cmd_Discordlink extends MyMaidLibrary implements CommandExecutor, CommandPremise {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
			return true;
		}
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();

		if (args.length != 1) {
			SendMessage(sender, cmd, "引数は1つのみにしてください。(/discordlink <AuthID>)");
			return true;
		}
		String permission = PermissionsManager.getPermissionMainGroup(player);
		String AuthKey = args[0];

		// AuthKeyは「半角英数字」で構成されているか？
		if (!AuthKey.matches("^[0-9a-zA-Z]+$")) {
			SendMessage(sender, cmd, "AuthKeyは英数字のみ受け付けています。");
			return true;
		}

		int id;
		String name, disid, discriminator;
		Connection conn;

		try {
			conn = MyMaidConfig.getMySQLDBManager().getConnection();

			// 指定されたAuthKeyは存在するか？
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM discordlink_waiting WHERE authkey = ?");
			statement.setString(1, AuthKey);
			ResultSet res = statement.executeQuery();

			if (!res.next()) {
				SendMessage(sender, cmd, "指定されたAuthIDは見つかりませんでした。");
				return true;
			}

			id = res.getInt("id");
			name = res.getString("name");
			disid = res.getString("disid");
			discriminator = res.getString("discriminator");
			statement.close();
		} catch (SQLException e) {
			ErrorReporter.report(e);
			SendMessage(sender, cmd, "操作に失敗しました。");
			SendMessage(sender, cmd, "詳しくはサーバコンソールをご確認ください");
			SendMessage(sender, cmd, "再度実行しなおすと動作するかもしれません。");
			return true;
		}

		try {
			// すでにリンク要求されたMinecraftアカウントと紐づいているか？
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM discordlink WHERE uuid = ? AND disid = ? AND disabled = ?");
			statement.setString(1, uuid);
			statement.setString(2, disid);
			statement.setInt(3, 0);
			ResultSet res = statement.executeQuery();

			if (res.next()) {
				SendMessage(sender, cmd, "すでにあなたのMinecraftアカウントと接続されています。");
				return true;
			}
			statement.close();
		} catch (SQLException e) {
			ErrorReporter.report(e);
			SendMessage(sender, cmd, "操作に失敗しました。");
			SendMessage(sender, cmd, "詳しくはサーバコンソールをご確認ください");
			SendMessage(sender, cmd, "再度実行しなおすと動作するかもしれません。");
			return true;
		}

		try {
			// リンク要求されたMinecraftアカウントが別のDiscordアカウントと紐づいていないか？
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM discordlink WHERE uuid = ? AND disabled = ?");
			statement.setString(1, uuid);
			statement.setInt(2, 0);
			ResultSet res = statement.executeQuery();

			if (res.next()) {
				SendMessage(sender, cmd, "すでにあなたのMinecraftアカウントは別のDiscordアカウントに接続されています。");
				return true;
			}
			statement.close();
		} catch (SQLException e) {
			ErrorReporter.report(e);
			SendMessage(sender, cmd, "操作に失敗しました。");
			SendMessage(sender, cmd, "詳しくはサーバコンソールをご確認ください");
			SendMessage(sender, cmd, "再度実行しなおすと動作するかもしれません。");
			return true;
		}

		try {
			// Discordアカウントが別のMinecraftアカウントと紐づいていないか？
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM discordlink WHERE disid = ? AND disabled = ?");
			statement.setString(1, disid);
			statement.setInt(2, 0);
			ResultSet res = statement.executeQuery();

			if (res.next()) {
				SendMessage(sender, cmd, "アカウントリンク要求をしたDiscordアカウントは既に他のMinecraftアカウントと接続されています。");
				return true;
			}
			statement.close();
		} catch (SQLException e) {
			ErrorReporter.report(e);
			SendMessage(sender, cmd, "操作に失敗しました。");
			SendMessage(sender, cmd, "詳しくはサーバコンソールをご確認ください");
			SendMessage(sender, cmd, "再度実行しなおすと動作するかもしれません。");
			return true;
		}

		// DiscordアカウントがDiscordチャンネルから退出していないかどうか
		JDA jda = MyMaidConfig.getJDA();
		Guild guild = jda.getGuildById(597378876556967936L);
		Member member = guild.getMemberById(disid);
		if (member == null) {
			SendMessage(sender, cmd, "アカウントリンク要求をしたDiscordアカウントは既に当サーバのDiscordチャンネルから退出しています。");
			return true;
		}

		try {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM discordlink_waiting WHERE id = ?");
			statement.setInt(1, id);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			ErrorReporter.report(e);
			SendMessage(sender, cmd, "操作に失敗しました。");
			SendMessage(sender, cmd, "詳しくはサーバコンソールをご確認ください");
			SendMessage(sender, cmd, "再度実行しなおすと動作するかもしれません。");
			return true;
		}

		try {
			PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO discordlink (player, uuid, name, disid, discriminator, pex) VALUES (?, ?, ?, ?, ?, ?);");
			statement.setString(1, player.getName());
			statement.setString(2, uuid);
			statement.setString(3, name);
			statement.setString(4, disid);
			statement.setString(5, discriminator);
			statement.setString(6, permission);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			ErrorReporter.report(e);
			SendMessage(sender, cmd, "操作に失敗しました。");
			SendMessage(sender, cmd, "詳しくはサーバコンソールをご確認ください");
			SendMessage(sender, cmd, "再度実行しなおすと動作するかもしれません。");
			return true;
		}

		SendMessage(sender, cmd, "アカウントのリンクが完了しました。");
		jda.getTextChannelById(597419057251090443L).sendMessage(
				":loudspeaker:<@" + disid + ">さんのMinecraftアカウント連携を完了しました！ MinecraftID: `" + player.getName() + "`")
				.queue();
		PermissionsManager.setPermissionsGroup(player, "verified");

		guild.addRoleToMember(member, guild.getRoleById(604011598952136853L)).queue(); // MinecraftConnected
		guild.addRoleToMember(member, guild.getRoleById(597405176969560064L)).queue(); // Verified
		return true;
	}

	@Override
	public String getDescription() {
		return "DiscordアカウントとMinecraftアカウントを紐づけます。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/discordlink <AuthKey>");
			}
		};
	}

}
