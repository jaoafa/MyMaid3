package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Cmd_Test extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		World World = Bukkit.getServer().getWorld("Jao_Afa");
		Location prison = new Location(World, 2856, 69, 2888);
		if (!player.getLocation().getWorld().getUID().equals(World.getUID())) {
			player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたは南の楽園から出られません！");
			// ワールド違い
			if (!player.teleport(prison, TeleportCause.PLUGIN)) {
				// 失敗時
				Location oldBed = player.getBedSpawnLocation();
				player.setBedSpawnLocation(prison, true);
				player.setHealth(0);
				player.setBedSpawnLocation(oldBed, true);
			}
			return true;
		}
		double distance = prison.distance(player.getLocation());
		sender.sendMessage("distance: " + distance);
		if (distance >= 40D) {
			player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたは南の楽園から出られません！");
			sender.sendMessage("40");
			if (distance >= 50D) {
				sender.sendMessage("50");
				if (!player.teleport(prison, TeleportCause.PLUGIN)) {
					// 失敗時
					sender.sendMessage("erro");
					Location oldBed = player.getBedSpawnLocation();
					player.setBedSpawnLocation(prison, true);
					player.setHealth(0);
					player.setBedSpawnLocation(oldBed, true);
				}
			}
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
