package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
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
		for (ChatColor cc : ChatColor.values()) {
			cc.name();

		}
		return true;
	}

	@Override
	public String getDescription() {
		return "test";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/test");
			}
		};
	}
}
