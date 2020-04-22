package com.jaoafa.MyMaid3.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.EBan;
import com.jaoafa.MyMaid3.Lib.Jail;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.jaoafa.MyMaid3.Task.Task_jaoiumAutoJailRelease;
import com.jaoafa.jaoSuperAchievement2.API.AchievementAPI;
import com.jaoafa.jaoSuperAchievement2.API.Achievementjao;
import com.jaoafa.jaoSuperAchievement2.Lib.AchievementType;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class Event_Antijaoium extends MyMaidLibrary implements Listener {
	List<Integer> HEAL_jaoium = new ArrayList<>();
	List<Integer> HEALTH_BOOST_jaoium = new ArrayList<>();

	public Event_Antijaoium() {
		HEAL_jaoium.add(-3);
		HEAL_jaoium.add(29);
		HEAL_jaoium.add(125);
		HEAL_jaoium.add(253);

		HEALTH_BOOST_jaoium.add(-7);
	}

	/**
	 * jaoiumと判定されるアイテムかどうか
	 * @param list PotionEffectのList
	 * @return jaoiumかどうか
	 * @author mine_book000
	 */
	private boolean isjaoium(List<PotionEffect> list) {
		boolean jaoium = false;
		for (PotionEffect po : list) {
			if (po.getType().equals(PotionEffectType.HEAL)) {
				if (HEAL_jaoium.contains(po.getAmplifier())) {
					// アウト
					jaoium = true;
				}
			}
			if (po.getType().equals(PotionEffectType.HEALTH_BOOST)) {
				if (HEALTH_BOOST_jaoium.contains(po.getAmplifier())) {
					// アウト
					jaoium = true;
				}
			}
		}
		return jaoium;
	}

	private String isMalicious(PotionMeta potion) {
		if (!potion.hasDisplayName()) {
			return null;
		}
		if (potion.getDisplayName().contains("§4§lDEATH")) {
			// Wurst?
			return "Wurst";
		}
		return null;
	}

	// -------------- ↓jaoium取得方法判定↓ -------------- //
	Map<String, String> Reason = new HashMap<>(); // プレイヤー : 理由

	@EventHandler(priority = EventPriority.MONITOR)
	public void ByPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String command = event.getMessage();

		if (!command.startsWith("/give")) {
			return;
		}
		if (command.equalsIgnoreCase("/give")) {
			return;
		}
		String[] commands = command.split(" ", 0);
		if (commands.length < 3) {
			return;
		}

		String item = commands[2];
		if (!item.equalsIgnoreCase("splash_potion") && !item.equalsIgnoreCase("minecraft:splash_potion")) {
			return;
		}

		String selector = commands[1];
		boolean SelectorToMe = false;
		boolean ALLPlayer = false;
		String ToPlayer = "";
		if (selector.equalsIgnoreCase("@p")) {
			// 自分
			SelectorToMe = true;
		} else if (selector.equalsIgnoreCase(player.getName())) {
			// 自分
			SelectorToMe = true;
		} else if (selector.equalsIgnoreCase("@a")) {
			// 自分(プレイヤーすべて)
			SelectorToMe = true;
			ALLPlayer = true;
		} else if (selector.equalsIgnoreCase("@e")) {
			// 自分(エンティティすべて)
			SelectorToMe = true;
			ALLPlayer = true;
		} else if (selector.equalsIgnoreCase("@s")) {
			// 自分(実行者)
			SelectorToMe = true;
		} else {
			Player p = Bukkit.getPlayer(selector);
			if (p != null) {
				ToPlayer = selector;
			}
		}
		if (SelectorToMe) {
			Reason.put(player.getName(), player.getName() + "の実行したコマンド : " + command);
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (ToPlayer.equalsIgnoreCase(p.getName())) {
				Reason.put(p.getName(), player.getName() + "の実行したコマンド : " + command);
				continue;
			}
			if (ALLPlayer) {
				Reason.put(p.getName(), player.getName() + "の実行したコマンド : " + command);
				continue;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void ByCommandBlock(ServerCommandEvent event) {
		if (!(event.getSender() instanceof BlockCommandSender))
			return;
		BlockCommandSender sender = (BlockCommandSender) event.getSender();

		if (sender.getBlock() == null || !(sender.getBlock().getState() instanceof CommandBlock))
			return;
		CommandBlock cmdb = (CommandBlock) sender.getBlock().getState();

		String command = cmdb.getCommand();
		if (!command.startsWith("/give") && !command.startsWith("give")) {
			return;
		}
		if (command.equalsIgnoreCase("/give") || command.equalsIgnoreCase("give")) {
			return;
		}
		String[] commands = command.split(" ", 0);
		if (commands.length < 3) {
			return;
		}

		String item = commands[2];
		if (!item.equalsIgnoreCase("splash_potion") && !item.equalsIgnoreCase("minecraft:splash_potion")) {
			return;
		}

		String selector = commands[1];
		boolean ALLPlayer = false;
		String ToPlayer = null;
		if (selector.equalsIgnoreCase("@p")) {
			// 一番近い
			Player p = getNearestPlayer(cmdb.getLocation());
			if (p == null) {
				return;
			}
			ToPlayer = p.getName();
		} else if (selector.equalsIgnoreCase("@a")) {
			// プレイヤーすべて
			ALLPlayer = true;
		} else if (selector.equalsIgnoreCase("@e")) {
			// エンティティすべて
			ALLPlayer = true;
		} else {
			Player p = Bukkit.getPlayer(selector);
			if (p != null) {
				ToPlayer = selector;
			}
		}
		if (ToPlayer == null) {
			return;
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (ToPlayer.equalsIgnoreCase(p.getName())) {
				Reason.put(p.getName(),
						"コマンドブロック(" + cmdb.getLocation().getWorld().getName() + " " + cmdb.getLocation().getBlockX()
								+ " " + cmdb.getLocation().getBlockY() + " " + cmdb.getLocation().getBlockZ()
								+ ")の実行したコマンド : " + command);
				continue;
			}
			if (ALLPlayer) {
				Reason.put(p.getName(),
						"コマンドブロック(" + cmdb.getLocation().getWorld().getName() + " " + cmdb.getLocation().getBlockX()
								+ " " + cmdb.getLocation().getBlockY() + " " + cmdb.getLocation().getBlockZ()
								+ ")の実行したコマンド : " + command);
				continue;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void ByItemPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		Item item = event.getItem();
		ItemStack hand = item.getItemStack();
		if (hand == null) {
			return;
		}
		if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
			PotionMeta potion = (PotionMeta) hand.getItemMeta();
			if (isjaoium(potion.getCustomEffects())) {
				Reason.put(player.getName(),
						player.getLocation().getWorld().getName() + " " + player.getLocation().getBlockX() + " "
								+ player.getLocation().getBlockY() + " " + player.getLocation().getBlockZ()
								+ "にて拾ったアイテム");
			}
		}
	}

	@EventHandler
	public void PlayerCreativeInv(InventoryCreativeEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack hand = event.getCurrentItem();
		if (hand == null) {
			return;
		}
		if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
			PotionMeta potion = (PotionMeta) hand.getItemMeta();
			if (isjaoium(potion.getCustomEffects())) {
				Reason.put(player.getName(), "クリエイティブインベントリから取得したアイテム(保存したツールバーからの可能性あり)　DebugDATA: "
						+ event.getAction().name() + " / " + event.getClick().name() + " / " + event.getHotbarButton());
			}
		}
	}

	/**
	 * 指定されたLocationに一番近いプレイヤーを取得します。
	 * @param loc Location
	 * @return 一番近いプレイヤー
	 */
	public Player getNearestPlayer(Location loc) {
		double closest = Double.MAX_VALUE;
		Player closestp = null;
		for (Player i : loc.getWorld().getPlayers()) {
			double dist = i.getLocation().distance(loc);
			if (closest == Double.MAX_VALUE || dist < closest) {
				closest = dist;
				closestp = i;
			}
		}
		if (closestp == null) {
			return null;
		}
		return closestp;
	}

	// -------------- ↑jaoium取得方法判定↑ -------------- //

	@EventHandler(priority = EventPriority.HIGHEST)
	public void ItemPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		Item item = event.getItem();
		ItemStack hand = item.getItemStack();
		if (hand == null) {
			return;
		}
		if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
			PotionMeta potion = (PotionMeta) hand.getItemMeta();
			if (isjaoium(potion.getCustomEffects())) {
				player.sendMessage("[jaoium_Checker] " + ChatColor.GREEN
						+ "あなたはjaoiumを拾いました。何か行動をする前に/clearをしないと、自動的に投獄されてしまうかもしれません！");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void InvClick(InventoryClickEvent event) throws ClassNotFoundException, SQLException {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getInventory();
		Inventory clickedinventory = event.getClickedInventory();
		ItemStack[] is = inventory.getContents();
		Jail jail = new Jail(player);
		EBan eban = new EBan(player);
		if (jail.isBanned()) {
			return;
		}
		boolean jaoium = false;
		String malicious = null;
		for (int n = 0; n < is.length; n++) {
			if (is[n] == null) {
				continue;
			}
			ItemStack hand = is[n];
			if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
				PotionMeta potion = (PotionMeta) hand.getItemMeta();
				boolean _jaoium = isjaoium(potion.getCustomEffects());
				if (_jaoium) {
					setjaoiumItemData(player, hand);
					if (inventory.getItem(n) != null)
						inventory.clear(n);
					jaoium = _jaoium;

					if (isMalicious(potion) != null) {
						malicious = isMalicious(potion);
					}
				}
			}
		}
		if (jaoium) {
			inventory.clear();
		}
		if (clickedinventory != null) {
			is = clickedinventory.getContents();
			for (int n = 0; n < is.length; n++) {
				if (is[n] == null) {
					continue;
				}
				ItemStack hand = is[n];
				if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
					PotionMeta potion = (PotionMeta) hand.getItemMeta();
					boolean _jaoium = isjaoium(potion.getCustomEffects());
					if (_jaoium) {
						setjaoiumItemData(player, hand);
						clickedinventory.clear(n);
						jaoium = _jaoium;

						if (isMalicious(potion) != null) {
							malicious = isMalicious(potion);
						}
					}
				}
			}
			if (jaoium) {
				clickedinventory.clear();
			}
		}
		if (jaoium && (jail.isBanned() || eban.isBanned())) {
			return;
		}
		if (jaoium) {
			Bukkit.broadcastMessage("[jaoium_Checker] " + ChatColor.GREEN + "プレイヤー「" + player.getName()
					+ "」からjaoiumと同等の性能を持つアイテムが検出されました。");
			checkjaoiumLocation(player);
			if (!Achievementjao.getAchievement(player, new AchievementType(23))) {
				player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
			}
			try {
				if (malicious != null) {
					eban.addBan("jaotan", "禁止クライアントMod「" + malicious + "」使用の疑い。方針「クライアントModの導入・利用に関する規則」の「禁止事項」への違反");
				} else {
					jail.addBan("jaotan", "jaoium所持");
					player.getInventory().clear();
					new Task_jaoiumAutoJailRelease(player).runTaskLater(JavaPlugin(), 1200L); // 60s
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) throws ClassNotFoundException, SQLException {
		Player player = event.getPlayer();
		Inventory inventory = player.getInventory();
		Inventory enderchestinventory = player.getEnderChest();
		ItemStack[] is = inventory.getContents();
		Jail jail = new Jail(player);
		EBan eban = new EBan(player);
		if (jail.isBanned()) {
			return;
		}
		boolean jaoium = false;
		String malicious = null;
		for (int n = 0; n < is.length; n++) {
			if (is[n] == null) {
				continue;
			}
			ItemStack hand = is[n];
			if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
				PotionMeta potion = (PotionMeta) hand.getItemMeta();
				boolean _jaoium = isjaoium(potion.getCustomEffects());
				if (_jaoium) {
					setjaoiumItemData(player, hand);
					inventory.clear(n);
					jaoium = _jaoium;

					if (isMalicious(potion) != null) {
						malicious = isMalicious(potion);
					}
				}
			}
		}
		if (jaoium) {
			inventory.clear();
		}

		boolean enderjaoium = false;
		if (enderchestinventory != null) {
			is = enderchestinventory.getContents();
			for (int n = 0; n < is.length; n++) {
				if (is[n] == null) {
					continue;
				}
				ItemStack hand = is[n];
				if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
					PotionMeta potion = (PotionMeta) hand.getItemMeta();
					boolean _enderjaoium = isjaoium(potion.getCustomEffects());
					if (_enderjaoium) {
						setjaoiumItemData(player, hand);
						enderchestinventory.clear(n);
						enderjaoium = _enderjaoium;

						if (isMalicious(potion) != null) {
							malicious = isMalicious(potion);
						}
					}
				}
			}
			if (enderjaoium) {
				enderchestinventory.clear();
			}
		}
		if ((jaoium || enderjaoium) && (jail.isBanned() || eban.isBanned())) {
			return;
		}
		if (jaoium || enderjaoium) {
			Bukkit.broadcastMessage("[jaoium_Checker] " + ChatColor.GREEN + "プレイヤー「" + player.getName()
					+ "」からjaoiumと同等の性能を持つアイテムが検出されました。");
			checkjaoiumLocation(player);
			if (!Achievementjao.getAchievement(player, new AchievementType(23))) {
				player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
			}
			try {
				if (malicious != null) {
					eban.addBan("jaotan", "禁止クライアントMod「" + malicious + "」使用の疑い。方針「クライアントModの導入・利用に関する規則」の「禁止事項」への違反");
				} else {
					jail.addBan("jaotan", "jaoium所持");
					player.getInventory().clear();
					new Task_jaoiumAutoJailRelease(player).runTaskLater(JavaPlugin(), 1200L); // 60s
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Inventory inventory = player.getInventory();
		Inventory enderchestinventory = player.getEnderChest();
		ItemStack[] is = inventory.getContents();
		Jail jail = new Jail(player);
		EBan eban = new EBan(player);
		if (jail.isBanned()) {
			return;
		}
		boolean jaoium = false;
		String malicious = null;
		for (int n = 0; n < is.length; n++) {
			if (is[n] == null) {
				continue;
			}
			ItemStack hand = is[n];
			if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
				PotionMeta potion = (PotionMeta) hand.getItemMeta();
				boolean _jaoium = isjaoium(potion.getCustomEffects());
				if (_jaoium) {
					setjaoiumItemData(player, hand);
					inventory.clear(n);
					jaoium = _jaoium;

					if (isMalicious(potion) != null) {
						malicious = isMalicious(potion);
					}
				}
			}
		}
		if (jaoium) {
			inventory.clear();
		}
		boolean enderjaoium = false;
		if (enderchestinventory != null) {
			is = enderchestinventory.getContents();
			for (int n = 0; n < is.length; n++) {
				if (is[n] == null) {
					continue;
				}
				ItemStack hand = is[n];
				if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
					PotionMeta potion = (PotionMeta) hand.getItemMeta();
					boolean _enderjaoium = isjaoium(potion.getCustomEffects());
					if (_enderjaoium) {
						setjaoiumItemData(player, hand);
						enderchestinventory.clear(n);
						enderjaoium = _enderjaoium;

						if (isMalicious(potion) != null) {
							malicious = isMalicious(potion);
						}
					}
				}
			}
			if (jaoium || enderjaoium) {
				enderchestinventory.clear();
			}
		}
		if ((jaoium || enderjaoium) && (jail.isBanned() || eban.isBanned())) {
			return;
		}
		if (jaoium) {
			Bukkit.broadcastMessage("[jaoium_Checker] " + ChatColor.GREEN + "プレイヤー「" + player.getName()
					+ "」からjaoiumと同等の性能を持つアイテムが検出されました。");
			checkjaoiumLocation(player);
			if (!Achievementjao.getAchievement(player, new AchievementType(23))) {
				player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
			}
			try {
				if (malicious != null) {
					eban.addBan("jaotan", "禁止クライアントMod「" + malicious + "」使用の疑い。方針「クライアントModの導入・利用に関する規則」の「禁止事項」への違反");
				} else {
					jail.addBan("jaotan", "jaoium所持");
					player.getInventory().clear();
					new Task_jaoiumAutoJailRelease(player).runTaskLater(JavaPlugin(), 1200L); // 60s
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity().getShooter();
		Inventory inventory = player.getInventory();
		Inventory enderchestinventory = player.getEnderChest();
		ItemStack[] is = inventory.getContents();
		Jail jail = new Jail(player);
		EBan eban = new EBan(player);
		if (jail.isBanned()) {
			return;
		}
		boolean jaoium = false;
		String malicious = null;
		for (int n = 0; n < is.length; n++) {
			if (is[n] == null) {
				continue;
			}
			ItemStack hand = is[n];
			if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
				PotionMeta potion = (PotionMeta) hand.getItemMeta();
				boolean _jaoium = isjaoium(potion.getCustomEffects());
				if (_jaoium) {
					setjaoiumItemData(player, hand);
					inventory.clear(n);
					jaoium = _jaoium;

					if (isMalicious(potion) != null) {
						malicious = isMalicious(potion);
					}
				}
			}
		}
		if (jaoium) {
			inventory.clear();
		}
		boolean enderjaoium = false;
		if (enderchestinventory != null) {
			is = enderchestinventory.getContents();
			for (int n = 0; n < is.length; n++) {
				if (is[n] == null) {
					continue;
				}
				ItemStack hand = is[n];
				if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
					PotionMeta potion = (PotionMeta) hand.getItemMeta();
					boolean _enderjaoium = isjaoium(potion.getCustomEffects());
					if (_enderjaoium) {
						setjaoiumItemData(player, hand);
						enderchestinventory.clear(n);
						enderjaoium = _enderjaoium;

						if (isMalicious(potion) != null) {
							malicious = isMalicious(potion);
						}
					}
				}
			}
			if (enderjaoium) {
				enderchestinventory.clear();
			}
		}
		if ((jaoium || enderjaoium) && (jail.isBanned() || eban.isBanned())) {
			return;
		}
		if (jaoium) {
			Bukkit.broadcastMessage("[jaoium_Checker] " + ChatColor.GREEN + "プレイヤー「" + player.getName()
					+ "」からjaoiumと同等の性能を持つアイテムが検出されました。");
			checkjaoiumLocation(player);
			if (!Achievementjao.getAchievement(player, new AchievementType(23))) {
				player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
			}
			try {
				if (malicious != null) {
					eban.addBan("jaotan", "禁止クライアントMod「" + malicious + "」使用の疑い。方針「クライアントModの導入・利用に関する規則」の「禁止事項」への違反");
				} else {
					jail.addBan("jaotan", "jaoium所持");
					player.getInventory().clear();
					new Task_jaoiumAutoJailRelease(player).runTaskLater(JavaPlugin(), 1200L); // 60s
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity().getShooter();
		Inventory inventory = player.getInventory();
		Inventory enderchestinventory = player.getEnderChest();
		ItemStack[] is = inventory.getContents();
		Jail jail = new Jail(player);
		EBan eban = new EBan(player);
		if (jail.isBanned()) {
			return;
		}
		boolean jaoium = false;
		String malicious = null;
		for (int n = 0; n < is.length; n++) {
			if (is[n] == null) {
				continue;
			}
			ItemStack hand = is[n];
			if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
				PotionMeta potion = (PotionMeta) hand.getItemMeta();
				boolean _jaoium = isjaoium(potion.getCustomEffects());
				if (_jaoium) {
					setjaoiumItemData(player, hand);
					inventory.clear(n);
					jaoium = _jaoium;

					if (isMalicious(potion) != null) {
						malicious = isMalicious(potion);
					}
				}
			}
		}
		if (jaoium) {
			inventory.clear();
		}
		boolean enderjaoium = false;
		if (enderchestinventory != null) {
			is = enderchestinventory.getContents();
			for (int n = 0; n < is.length; n++) {
				if (is[n] == null) {
					continue;
				}
				ItemStack hand = is[n];
				if (hand.getType() == Material.SPLASH_POTION || hand.getType() == Material.LINGERING_POTION) {
					PotionMeta potion = (PotionMeta) hand.getItemMeta();
					boolean _enderjaoium = isjaoium(potion.getCustomEffects());
					if (_enderjaoium) {
						setjaoiumItemData(player, hand);
						enderchestinventory.clear(n);
						enderjaoium = _enderjaoium;

						if (isMalicious(potion) != null) {
							malicious = isMalicious(potion);
						}
					}
				}
			}
			if (enderjaoium) {
				enderchestinventory.clear();
			}
		}
		if ((jaoium || enderjaoium) && ((jail.isBanned() || eban.isBanned()))) {
			return;
		}
		if (jaoium) {
			Bukkit.broadcastMessage("[jaoium_Checker] " + ChatColor.GREEN + "プレイヤー「" + player.getName()
					+ "」からjaoiumと同等の性能を持つアイテムが検出されました。");
			checkjaoiumLocation(player);
			if (!Achievementjao.getAchievement(player, new AchievementType(23))) {
				player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
			}
			try {
				if (malicious != null) {
					eban.addBan("jaotan", "禁止クライアントMod「" + malicious + "」使用の疑い。方針「クライアントModの導入・利用に関する規則」の「禁止事項」への違反");
				} else {
					jail.addBan("jaotan", "jaoium所持");
					player.getInventory().clear();
					new Task_jaoiumAutoJailRelease(player).runTaskLater(JavaPlugin(), 1200L); // 60s
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void OnBlockDispenseEvent(BlockDispenseEvent event) {
		boolean jaoium = false;
		ItemStack is = event.getItem();
		if (is.getType() == Material.SPLASH_POTION || is.getType() == Material.LINGERING_POTION) {
			PotionMeta potion = (PotionMeta) is.getItemMeta();
			jaoium = isjaoium(potion.getCustomEffects());
		}
		if (jaoium) {
			event.setCancelled(true);
		}
	}

	private void checkjaoiumLocation(Player player) {
		Location loc = player.getLocation();
		String reason = "null";
		if (Reason.containsKey(player.getName())) {
			reason = Reason.get(player.getName());
			Reason.remove(player.getName());
		}
		String ItemDataUrl = "null";
		if (ItemData.containsKey(player.getName())) {
			ItemDataUrl = ItemData.get(player.getName());
			ItemData.remove(player.getName());
		}
		Main.getJDA().getTextChannelById(597423444501463040L).sendMessage("**jaoium Location & Reason Notice**\n"
				+ "Player: " + player.getName() + "\n"
				+ "Location: " + loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " "
				+ loc.getBlockZ() + "\n"
				+ "Reason: ``" + reason + "``\n"
				+ "ItemData: " + ItemDataUrl);
	}

	Map<String, String> ItemData = new HashMap<>();

	private void setjaoiumItemData(Player player, ItemStack is) {

		if (ItemData.containsKey(player.getName()))
			return;
		YamlConfiguration yaml = new YamlConfiguration();

		yaml.set("data", is);

		StringBuilder builder = new StringBuilder();
		builder.append("/give @p splash_potion "); // "/give @p <アイテム> "
		builder.append(is.getAmount()); // "/give @p <アイテム> [個数]"
		builder.append(" "); // "/give @p <アイテム> [個数] "
		builder.append(is.getDurability()); // "/give @p <アイテム> [個数] [データ]"
		net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
		NBTTagCompound nbttag = nmsItem.getTag();
		if (nbttag != null) {
			builder.append(" "); // "/give @p <アイテム> [個数] [データ] "
			builder.append(nbttag.toString()); // "/give @p <アイテム> [個数] [データ] [データタグ]"
		}

		String command = builder.toString();
		yaml.set("command", command);

		String code = yaml.saveToString();
		String name = "MyMaid3 Antijaoium jaoium ItemData & Command";
		try {
			MySQLDBManager MySQLDBManager = Main.getMySQLDBManager();
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO cmd (player, uuid, title, command) VALUES (?, ?, ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, player.getName());
			statement.setString(2, player.getUniqueId().toString());
			statement.setString(3, name);
			statement.setString(4, code);
			statement.executeUpdate();
			ResultSet res = statement.getGeneratedKeys();
			if (res == null || !res.next()) {
				throw new IllegalStateException();
			}
			int id = res.getInt(1);
			ItemData.put(player.getName(), "https://jaoafa.com/cmd/" + id);
		} catch (SQLException e) {
			ItemData.put(player.getName(), "null");
		}
	}
}