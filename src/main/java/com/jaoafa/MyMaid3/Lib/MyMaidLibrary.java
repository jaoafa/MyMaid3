package com.jaoafa.MyMaid3.Lib;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.MyMaid3.Main;

public class MyMaidLibrary {
	@Nullable
	public static JavaPlugin JavaPlugin() {
		return Main.getJavaPlugin();
	}

	/**
	 * CommandSenderに対してメッセージを送信します。
	 * @param sender CommandSender
	 * @param cmd Commandデータ
	 * @param message メッセージ
	 */
	public static void SendMessage(CommandSender sender, Command cmd, String message) {
		sender.sendMessage("[" + cmd.getName().toUpperCase() + "] " + ChatColor.GREEN + message);
	}

	/**
	 * CommandSenderに対してヘルプメッセージと使い方を送信します。
	 * @param sender
	 * @param cmd
	 */
	public void SendUsageMessage(CommandSender sender, Command cmd) {
		SendMessage(sender, cmd, "------- " + cmd.getName() + " --------");
		SendMessage(sender, cmd, cmd.getDescription());
		String CMDusage = cmd.getUsage();

		CMDusage = CMDusage.replaceAll("<command>", cmd.getName());

		if (CMDusage.contains("\n")) {
			String[] usages = CMDusage.split("\n");
			for (String usage : usages) {
				SendMessage(sender, cmd, usage);
			}
		} else {
			SendMessage(sender, cmd, CMDusage);
		}
	}

	public static String sdfFormat(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(date);
	}
}
