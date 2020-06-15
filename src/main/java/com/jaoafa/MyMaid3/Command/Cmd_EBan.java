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
import com.jaoafa.MyMaid3.Lib.EBan;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Cmd_EBan extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("status")) {
			// /eban status
			// /eban status mine_book000
			onCmd_Status(sender, cmd, args);
			return true;
		}

		if (args.length >= 3 && args[0].equalsIgnoreCase("add")) {
			// /eban remove mine_book000
			onCmd_Add(sender, cmd, args);
			return true;
		}
		if (args.length >= 2 && args[0].equalsIgnoreCase("remove")) {
			// /eban add mine_book000 test a b c
			onCmd_Remove(sender, cmd, args);
			return true;
		}
		SendUsageMessage(sender, cmd);
		return true;
	}

	private void onCmd_Status(CommandSender sender, Command cmd, String[] args) {
		if (args.length == 1) {
			Set<EBan> ebans = EBan.getList();
			SendMessage(sender, cmd, "現在、" + ebans.size() + "人のプレイヤーがEBanされています。");
			for (EBan eban : ebans) {
				String name;
				if (eban.getPlayer() == null || eban.getPlayer().getName() == null) {
					name = eban.getName();
				} else {
					name = eban.getPlayer().getName();
				}
				SendMessage(sender, cmd, name + " " + eban.getLastBanReason());
			}
		} else if (args.length == 2) {
			OfflinePlayer offplayer = getOfflinePlayer(args[1]);
			EBan eban = new EBan(offplayer);
			if (eban.isBanned()) {
				SendMessage(sender, cmd, "プレイヤー「" + offplayer.getName() + "」は現在EBanされています。");
				SendMessage(sender, cmd, "Banned_By: " + eban.getBannedBy());
				SendMessage(sender, cmd, "Reason: " + eban.getLastBanReason());
				SendMessage(sender, cmd, "Date: " + sdfFormat(eban.getBannedDate()));
			} else {
				SendMessage(sender, cmd, "プレイヤー「" + offplayer.getName() + "」は現在EBanされていません。");
			}
		}
	}

	private void onCmd_Add(CommandSender sender, Command cmd, String[] args) {
		if (sender instanceof Player) {
			Player commander = (Player) sender;
			String group = PermissionsManager.getPermissionMainGroup(commander);
			if (!group.equalsIgnoreCase("Moderator") && !group.equalsIgnoreCase("Admin")) {
				SendMessage(sender, cmd, ChatColor.RED + "このコマンドは、あなたの権限では使用できません。");
				return;
			}
		}
		String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		OfflinePlayer offplayer = getOfflinePlayer(args[1]);
		EBan eban = new EBan(offplayer);
		if (eban.isBanned()) {
			SendMessage(sender, cmd, ChatColor.GREEN + "このプレイヤーは現在EBanされているため、実行できません。");
			return;
		}
		if (eban.addBan(sender.getName(), reason)) {
			SendMessage(sender, cmd, ChatColor.RED + "実行に成功しました。");
		} else {
			SendMessage(sender, cmd, ChatColor.RED + "実行に失敗しました。");
		}
	}

	private void onCmd_Remove(CommandSender sender, Command cmd, String[] args) {
		if (sender instanceof Player) {
			Player commander = (Player) sender;
			String group = PermissionsManager.getPermissionMainGroup(commander);
			if (!group.equalsIgnoreCase("Moderator") && !group.equalsIgnoreCase("Admin")) {
				SendMessage(sender, cmd, ChatColor.RED + "このコマンドは、あなたの権限では使用できません。");
				return;
			}
		}
		OfflinePlayer offplayer = getOfflinePlayer(args[1]);
		EBan eban = new EBan(offplayer);
		if (!eban.isBanned()) {
			SendMessage(sender, cmd, ChatColor.RED + "指定されたプレイヤーはEBanされていません。");
			return;
		}
		if (eban.removeBan(sender.getName())) {
			SendMessage(sender, cmd, ChatColor.RED + "実行に成功しました。");
		} else {
			SendMessage(sender, cmd, ChatColor.RED + "実行に失敗しました。");
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
		return "EBanに関する処理を行います。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/eban add <Target> <Reason>: TargetをEBanします。");
				add("/eban remove <Target>: TargetのEBanを解除します。");
				add("/eban status: EBan一覧を表示します。");
				add("/eban status <Target>: TargetのEBan情報を表示します。");
			}
		};
	}
}
