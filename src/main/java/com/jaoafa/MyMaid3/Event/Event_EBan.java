package com.jaoafa.MyMaid3.Event;

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
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
import com.jaoafa.MyMaid3.Lib.EBan;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Event_EBan implements Listener {
	@EventHandler
	public void onEvent_ChatLiquidBounce(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		EBan eban = new EBan(player);
		eban.DBSync();

		if (!eban.isBanned()) {
			return;
		}
		if (!event.getMessage().contains("LiquidBounce Client | liquidbounce.net")) {
			return;
		}

		eban.addBan("jaotan", "禁止クライアント「LiquidBounce」使用の疑い。");
		event.setCancelled(true);

		// 必要に応じて自動ChatJail。
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void OnEvent_LoginEBanCheck(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		new BukkitRunnable() {
			public void run() {
				EBan eban = new EBan(player);
				eban.DBSync();

				if (!eban.isBanned()) {
					return;
				}
				String reason = eban.getLastBanReason();
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
							"[EBan] " + ChatColor.RED + "プレイヤー「" + player.getName() + "」は、「" + reason
									+ "」という理由でEBanされています。");
					p.sendMessage("[EBan] " + ChatColor.RED + "詳しい情報は /eban status " + player.getName() + " でご確認ください。");
				}
				player.sendMessage("[EBan] " + ChatColor.RED + "あなたは、「" + reason + "」という理由でEBanされています。");
				player.sendMessage("[EBan] " + ChatColor.RED + "解除申請の方法や、Banの方針などは以下ページをご覧ください。");
				player.sendMessage("[EBan] " + ChatColor.RED + "https://jaoafa.com/rule/management/punishment");
			}
		}.runTaskAsynchronously(Main.getJavaPlugin());
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) { // 南の楽園外に出られるかどうか
		Location to = event.getTo();
		Player player = event.getPlayer();
		EBan eban = new EBan(player.getUniqueId());
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
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
			return;
		}
		double distance = prison.distance(to);
		if (distance >= 40D) {
			player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたは南の楽園から出られません！");
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
		EBan eban = new EBan(player.getUniqueId());
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		World World = Bukkit.getServer().getWorld("Jao_Afa");
		Location prison = new Location(World, 2856, 69, 2888);
		event.setRespawnLocation(prison);
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!(player instanceof Player)) {
			return;
		}
		if (!player.getLocation().getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
			return;
		}
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたはブロックを置けません。");
		Bukkit.getLogger().info("[EBan] " + player.getName() + "==>あなたはブロックを置けません。");
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!(player instanceof Player)) {
			return;
		}
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたはブロックを壊せません。");
		Bukkit.getLogger().info("[EBan] " + player.getName() + "==>あなたはブロックを壊せません。");
	}

	@EventHandler
	public void onBlockIgniteEvent(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		if (!(player instanceof Player)) {
			return;
		}
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたはブロックを着火できません。");
		Bukkit.getLogger().info("[EBan] " + player.getName() + "==>あなたはブロックを着火できません。");
	}

	@EventHandler
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (!(player instanceof Player)) {
			return;
		}
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたは水や溶岩を撒けません。");
		Bukkit.getLogger().info("[EBan] " + player.getName() + "==>あなたは水や溶岩を撒けません。");
	}

	@EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		if (!(player instanceof Player)) {
			return;
		}
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたは水や溶岩を掬うことはできません。");
		Bukkit.getLogger().info("[EBan] " + player.getName() + "==>あなたは水や溶岩を掬うことはできません。");
	}

	@EventHandler
	public void onPlayerPickupItemEvent(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (!(player instanceof Player)) {
			return;
		}
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Player player = (Player) event.getPlayer();
		if (!(player instanceof Player)) {
			return;
		}
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (!(player instanceof Player)) {
			return;
		}
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
		player.sendMessage("[EBan] " + ChatColor.GREEN + "あなたはコマンドを実行できません。");
		Bukkit.getLogger().info("[EBan] " + player.getName() + "==>あなたはコマンドを実行できません。");
	}

	@EventHandler
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity().getShooter();
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
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
		EBan eban = new EBan(player);
		if (!eban.isBanned()) { // EBanされてる
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onQuitClearCache(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		new BukkitRunnable() {
			public void run() {
				EBan eban = new EBan(player);
				eban.DBSync();
			}
		}.runTaskAsynchronously(Main.getJavaPlugin());
	}
}
