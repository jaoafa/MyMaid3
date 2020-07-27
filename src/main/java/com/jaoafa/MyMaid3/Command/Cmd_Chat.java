package com.jaoafa.MyMaid3.Command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Cmd_Chat extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (args.length < 2) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		ChatColor color = ChatColor.GRAY;
		List<String> colors = Arrays.stream(args).filter(
				arg -> arg != null && arg.startsWith("color:")).collect(Collectors.toList());
		if (colors.size() != 0) {
			for (ChatColor cc : ChatColor.values()) {
				if (!cc.name().equalsIgnoreCase(colors.get(0).substring("color:".length()))) {
					continue;
				}
				color = cc;
			}
		}
		List<String> texts = Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).filter(
				arg -> arg != null && !arg.startsWith("color:")).collect(Collectors.toList());
		String text = ChatColor.translateAlternateColorCodes('&', String.join(" ", texts));
		if (args[0].equalsIgnoreCase("jaotan")) {
			color = ChatColor.GOLD;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		Bukkit.broadcastMessage(ChatColor.GRAY + "[" + sdf.format(new Date()) + "]" + color + "■" + ChatColor.WHITE
				+ args[0] + ": " + text);
		MyMaidConfig.getServerChatChannel()
				.sendMessage("**" + DiscordEscape(args[0]) + "**: " + DiscordEscape(ChatColor.stripColor(text)))
				.queue();
		//DiscordSend("**" + args[0] + "**: " + text);
		return true;
	}

	@Override
	public String getDescription() {
		return "偽のプレイヤーをしゃべらせます。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/chat <FakePlayer> <Message>");
			}
		};
	}
}
