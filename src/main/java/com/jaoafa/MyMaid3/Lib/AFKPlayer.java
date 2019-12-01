package com.jaoafa.MyMaid3.Lib;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import com.connorlinfoot.titleapi.TitleAPI;
import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Task.Task_AFKING;

public class AFKPlayer {
	static Map<String, AFKPlayer> players = new HashMap<>();

	private Player player;
	private boolean isAFKing = false;
	private long AFKStartTime = -1L;
	private BukkitTask Task = null;
	private ItemStack HeadItem = null;
	private long LastActionTime = -1L;

	public AFKPlayer(Player player) {
		if (players.containsKey(player.getName())) {
			AFKPlayer afkplayer = players.get(player.getName());
			this.player = afkplayer.player;
			this.isAFKing = afkplayer.isAFKing;
			this.AFKStartTime = afkplayer.AFKStartTime;
			this.Task = afkplayer.Task;
			this.HeadItem = afkplayer.HeadItem;
			this.LastActionTime = afkplayer.LastActionTime;
			return;
		}
		this.player = player;
		players.put(player.getName(), this);
	}

	public void start() {
		if (isAFK()) {
			return;
		}
		isAFKing = true;
		AFKStartTime = System.currentTimeMillis() / 1000L;

		PlayerInventory playerinv = player.getInventory();
		HeadItem = playerinv.getHelmet();
		player.getInventory().setHelmet(new ItemStack(Material.ICE));
		player.updateInventory();

		String listname = player.getPlayerListName().replaceAll(player.getName(),
				ChatColor.DARK_GRAY + player.getName());
		player.setPlayerListName(listname);

		TitleAPI.sendTitle(player, 0, 99999999, 0, ChatColor.RED + "AFK NOW!",
				ChatColor.BLUE + "" + ChatColor.BOLD + "When you are back, please enter the command '/afk' or Move.");

		Bukkit.broadcastMessage(ChatColor.DARK_GRAY + player.getName() + " is afk!");
		if (Main.ServerChatChannel != null) {
			Main.ServerChatChannel.sendMessage(player.getName() + " is afk!").queue();
		}

		try {
			Task = new Task_AFKING(player).runTaskTimer(Main.getJavaPlugin(), 0L, 5L);
		} catch (NoClassDefFoundError e) {
			ErrorReporter.report(e);
			Task = null;
		}

		players.put(player.getName(), this);
	}

	public void end() {
		isAFKing = false;
		if (Task == null || Task.isCancelled()) {
			Task.cancel();
		}
		PlayerInventory playerinv = player.getInventory();
		if (HeadItem != null) {
			if (HeadItem.getType() == Material.ICE)
				HeadItem = new ItemStack(Material.AIR);
			playerinv.setHelmet(HeadItem);
		} else {
			playerinv.setHelmet(new ItemStack(Material.AIR));
		}
		HeadItem = null;

		String listname = player.getPlayerListName().replaceAll(player.getName(), ChatColor.WHITE + player.getName());
		player.setPlayerListName(listname);

		Bukkit.broadcastMessage(ChatColor.DARK_GRAY + player.getName() + " is now online!");
		if (Main.ServerChatChannel != null) {
			Main.ServerChatChannel.sendMessage(player.getName() + " is now online!").queue();
		}

		TitleAPI.clearTitle(player);

		players.put(player.getName(), this);
	}

	public void clear() {
		if (isAFKing) {
			end();
		}
		if (players.containsKey(player.getName())) {
			players.remove(player.getName());
		}
	}

	public boolean isAFK() {
		if (Task == null || Task.isCancelled()) {
			isAFKing = false;
			players.put(player.getName(), this);
			return false;
		}
		return isAFKing;
	}

	public long getAFKingSec() {
		return (System.currentTimeMillis() / 1000L) - AFKStartTime;
	}

	public long getAFKStartTime() {
		return AFKStartTime;
	}

	public long getLastActionTime() {
		return LastActionTime;
	}

	public void setNowLastActionTime() {
		LastActionTime = System.currentTimeMillis();
		players.put(player.getName(), this);
	}

	public static Map<String, AFKPlayer> getAFKPlayers() {
		return players;
	}
}
