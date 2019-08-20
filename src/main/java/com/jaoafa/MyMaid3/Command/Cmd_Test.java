package com.jaoafa.MyMaid3.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jaoafa.MyMaid3.CommandPremise;

public class Cmd_Test implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage("test");
		return true;
	}

	@Override
	public String getDescription() {
		return "test";
	}

	@Override
	public String getUsage() {
		return "/test";
	}
}
