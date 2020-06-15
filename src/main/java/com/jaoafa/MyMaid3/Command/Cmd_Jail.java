package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.Jail;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Cmd_Jail extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("status")) {
			// /jail status
			// /jail status mine_book000
			onCmd_Status(sender, cmd, args);
			return true;
		}

		if (args.length >= 3 && args[0].equalsIgnoreCase("add")) {
			// /jail add mine_book000 test a b c
			onCmd_Add(sender, cmd, args);
			return true;
		}
		if (args.length >= 2 && args[0].equalsIgnoreCase("remove")) {
			// /jail remove mine_book000
			onCmd_Remove(sender, cmd, args);
			return true;
		}
		if (args.length >= 2 && args[0].equalsIgnoreCase("testment")) {
			// /jail testment aaaaa
			onCmd_Testment(sender, cmd, args);
			return true;
		}
		SendUsageMessage(sender, cmd);
		return true;
	}

	private void onCmd_Status(CommandSender sender, Command cmd, String[] args) {
		if (args.length == 1) {
			Set<Jail> jails = Jail.getList();
			SendMessage(sender, cmd, "現在、" + jails.size() + "人のプレイヤーがJailされています。");
			for (Jail jail : jails) {
				String name;
				if (jail.getPlayer() == null || jail.getPlayer().getName() == null) {
					name = jail.getName();
				} else {
					name = jail.getPlayer().getName();
				}
				SendMessage(sender, cmd, name + " " + jail.getLastBanReason());
			}
		} else if (args.length == 2) {
			OfflinePlayer offplayer = getOfflinePlayer(args[1]);
			Jail jail = new Jail(offplayer);
			if (jail.isBanned()) {
				SendMessage(sender, cmd, "プレイヤー「" + offplayer.getName() + "」は現在Jailされています。");
				SendMessage(sender, cmd, "Banned_By: " + jail.getBannedBy());
				SendMessage(sender, cmd, "Reason: " + jail.getLastBanReason());
				SendMessage(sender, cmd, "Date: " + sdfFormat(jail.getBannedDate()));
			} else {
				SendMessage(sender, cmd, "プレイヤー「" + offplayer.getName() + "」は現在Jailされていません。");
			}
		}
	}

	private void onCmd_Add(CommandSender sender, Command cmd, String[] args) {
		if (sender instanceof Player) {
			Player commander = (Player) sender;
			String group = PermissionsManager.getPermissionMainGroup(commander);
			if (!group.equalsIgnoreCase("Regular") && !group.equalsIgnoreCase("Moderator")
					&& !group.equalsIgnoreCase("Admin")) {
				SendMessage(sender, cmd, ChatColor.GREEN + "このコマンドは、あなたの権限では使用できません。");
				return;
			}
		}
		String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		OfflinePlayer offplayer = getOfflinePlayer(args[1]);
		Jail jail = new Jail(offplayer);
		if (jail.isBanned()) {
			SendMessage(sender, cmd, ChatColor.GREEN + "このプレイヤーは現在Jailされているため、実行できません。");
			return;
		}
		if (jail.addBan(sender.getName(), reason)) {
			SendMessage(sender, cmd, ChatColor.GREEN + "実行に成功しました。");
		} else {
			SendMessage(sender, cmd, ChatColor.GREEN + "実行に失敗しました。");
		}
	}

	private void onCmd_Remove(CommandSender sender, Command cmd, String[] args) {
		if (sender instanceof Player) {
			Player commander = (Player) sender;
			String group = PermissionsManager.getPermissionMainGroup(commander);
			if (!group.equalsIgnoreCase("Regular") && !group.equalsIgnoreCase("Moderator")
					&& !group.equalsIgnoreCase("Admin")) {
				SendMessage(sender, cmd, ChatColor.GREEN + "このコマンドは、あなたの権限では使用できません。");
				return;
			}
		}
		OfflinePlayer offplayer = getOfflinePlayer(args[1]);
		Jail jail = new Jail(offplayer);
		if (!jail.isBanned()) {
			SendMessage(sender, cmd, ChatColor.GREEN + "指定されたプレイヤーはJailされていません。");
			return;
		}
		if (jail.removeBan(sender.getName())) {
			SendMessage(sender, cmd, ChatColor.GREEN + "実行に成功しました。");
		} else {
			SendMessage(sender, cmd, ChatColor.GREEN + "実行に失敗しました。");
		}
	}

	private void onCmd_Testment(CommandSender sender, Command cmd, String[] args) {
		if (!(sender instanceof Player)) {
			return;
		}
		Player commander = (Player) sender;
		Jail jail = new Jail(commander);
		if (!jail.isBanned()) {
			SendMessage(sender, cmd, ChatColor.GREEN + "あなたはJailされていないので遺言を書くことはできません。");
			return;
		}
		if (jail.getLastBanTestment() != null) {
			SendMessage(sender, cmd, ChatColor.GREEN + "あなたは既に遺言を記録しています。");
			return;
		}
		String testment = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		if (jail.setTestment(testment)) {
			SendMessage(sender, cmd, ChatColor.GREEN + "遺言を記録しました。");
		} else {
			SendMessage(sender, cmd, ChatColor.GREEN + "遺言の記録に失敗しました。");
		}
	}

	@SuppressWarnings("deprecation")
	private OfflinePlayer getOfflinePlayer(String name_or_uuid) {
		if (name_or_uuid.contains("-")) {
			return Bukkit.getOfflinePlayer(UUID.fromString(name_or_uuid));
		} else {
			return Bukkit.getOfflinePlayer(name_or_uuid);
		}
	}

	@Override
	public String getDescription() {
		return "Jailに関する処理を行います。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/jail add <Target> <Reason>: TargetをJailします。");
				add("/jail remove <Target>: TargetのJailを解除します。");
				add("/jail status: Jail一覧を表示します。");
				add("/jail status <Target>: TargetのJail情報を表示します。");
				add("/jail testment <Testment>: 遺言を記録します。");
			}
		};
	}
}
