package com.jaoafa.MyMaid3.Event;

import com.jaoafa.jaoSuperAchievement2.API.Achievementjao;
import com.jaoafa.jaoSuperAchievement2.Lib.AchievementType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.Jail;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Event_Jail implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void OnEvent_LoginJailCheck(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		new BukkitRunnable() {
			public void run() {
				Jail jail = new Jail(player.getUniqueId());
				if (!jail.isBanned()) {
					return;
				}
				String reason = jail.getLastBanReason();
				if (reason == null) {
					return;
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					String group = PermissionsManager.getPermissionMainGroup(p);
					if (!group.equalsIgnoreCase("Admin") && !group.equalsIgnoreCase("Moderator")
							&& !group.equalsIgnoreCase("Regular")) {
						continue;
					}
					p.sendMessage(
							"[Jail] " + ChatColor.GREEN + "プレイヤー「" + player.getName() + "」は、「" + reason
									+ "」という理由でJailされています。");
					p.sendMessage(
							"[Jail] " + ChatColor.GREEN + "詳しい情報は /jail status " + player.getName() + " でご確認ください。");
				}
				player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたは、「" + reason + "」という理由でJailされています。");
				player.sendMessage("[Jail] " + ChatColor.GREEN + "解除申請の方法や、Banの方針などは以下ページをご覧ください。");
				player.sendMessage("[Jail] " + ChatColor.GREEN + "https://jaoafa.com/rule/management/punishment");
			}
		}.runTaskAsynchronously(Main.getJavaPlugin());
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) { // 南の楽園外に出られるかどうか
		Location to = event.getTo();
		Player player = event.getPlayer();
		Jail jail = new Jail(player.getUniqueId());
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		World World = Bukkit.getServer().getWorld("Jao_Afa");
		Location prison = new Location(World, 2856, 69, 2888);
		if (!player.getLocation().getWorld().getUID().equals(World.getUID())) {
			player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたは南の楽園から出られません！");
			// ワールド違い
			if (!player.teleport(prison, TeleportCause.PLUGIN)) {
				// 失敗時
				Location oldBed = player.getBedSpawnLocation();
				player.setBedSpawnLocation(prison, true);
				player.setHealth(0);
				player.setBedSpawnLocation(oldBed, true);
			}
			return;
		}
		double distance = prison.distance(to);
		if (distance >= 40D) {
			player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたは南の楽園から出られません！");
			if (distance >= 50D) {
				if (!player.teleport(prison, TeleportCause.PLUGIN)) {
					// 失敗時
					Location oldBed = player.getBedSpawnLocation();
					player.setBedSpawnLocation(prison, true);
					player.setHealth(0);
					player.setBedSpawnLocation(oldBed, true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Jail jail = new Jail(player.getUniqueId());
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		World World = Bukkit.getServer().getWorld("Jao_Afa");
		Location prison = new Location(World, 2856, 69, 2888);
		event.setRespawnLocation(prison);
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		if (!player.getLocation().getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたはブロックを置けません。");
		Bukkit.getLogger().info("[Jail] " + player.getName() + "==>あなたはブロックを置けません。");
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたはブロックを壊せません。");
		Bukkit.getLogger().info("[Jail] " + player.getName() + "==>あなたはブロックを壊せません。");
	}

	@EventHandler
	public void onBlockIgniteEvent(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたはブロックを着火できません。");
		Bukkit.getLogger().info("[Jail] " + player.getName() + "==>あなたはブロックを着火できません。");
	}

	@EventHandler
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたは水や溶岩を撒けません。");
		Bukkit.getLogger().info("[Jail] " + player.getName() + "==>あなたは水や溶岩を撒けません。");
	}

	@EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたは水や溶岩を掬うことはできません。");
		Bukkit.getLogger().info("[Jail] " + player.getName() + "==>あなたは水や溶岩を掬うことはできません。");
	}

	@EventHandler
	public void onPlayerPickupItemEvent(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (player == null) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		String command = event.getMessage();
		String[] args = command.split(" ", 0);
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("/testment")) {
				return;
			}
		}
		if (args.length >= 3) {
			if (args[0].equalsIgnoreCase("/jail") && args[1].equalsIgnoreCase("testment")) {
				return;
			}
		}
		event.setCancelled(true);
		player.sendMessage("[Jail] " + ChatColor.GREEN + "あなたはコマンドを実行できません。");
		Bukkit.getLogger().info("[Jail] " + player.getName() + "==>あなたはコマンドを実行できません。");
	}

	@EventHandler
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity().getShooter();
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity().getShooter();
		Jail jail = new Jail(player);
		if (!jail.isBanned()) { // Jailされてる
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onJoinClearCache(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		new BukkitRunnable() {
			public void run() {
				Jail jail = new Jail(player);
				jail.DBSync();
			}
		}.runTaskAsynchronously(Main.getJavaPlugin());
	}

	@EventHandler
	public void onQuitClearCache(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Jail jail = new Jail(player);
		if (jail.isBanned()) { // Jailされてる
			Achievementjao.getAchievement(player, new AchievementType(69)); // 脱獄者だ！
		}
		new BukkitRunnable() {
			public void run() {
				Jail jail = new Jail(player);
				jail.DBSync();
			}
		}.runTaskAsynchronously(Main.getJavaPlugin());
	}
}
