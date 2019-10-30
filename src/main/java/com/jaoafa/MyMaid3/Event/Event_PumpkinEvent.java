package com.jaoafa.MyMaid3.Event;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.ErrorReporter;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class Event_PumpkinEvent extends MyMaidLibrary implements Listener {
	@EventHandler
	public void OnEvent_PumpkinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date start = format.parse("2019/10/31 00:00:00");
			Date end = format.parse("2019/10/31 23:59:59");
			if (!isPeriod(start, end)) {
				return;
			}
		} catch (ParseException e) {
			ErrorReporter.report(e);
			return;
		}

		ItemStack item = new ItemStack(Material.PUMPKIN_PIE);
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbttag = nms.getTag();
		if (nbttag == null) {
			nbttag = new NBTTagCompound();
		}
		UUID uuid = UUID.randomUUID();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			ErrorReporter.report(e);
			return;
		}
		byte[] digest = md.digest(uuid.toString().getBytes());
		String id = DatatypeConverter.printHexBinary(digest);
		if (id == null) {
			return;
		}
		nbttag.setString("MyMaid_2019PumpkinID", id);
		nms.setTag(nbttag);

		item = CraftItemStack.asBukkitCopy(nms);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("2019Pumpkin");
		List<String> lore = new ArrayList<String>();
		lore.add("2019年ハロウィンイベントのイベントアイテムです！持っておくと何かいいことがあるかも…？");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 999, true);
		item.setItemMeta(meta);

		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return;
		}
		try {
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO 2019pumpkin (id, type, player, uuid) VALUES (?, ?, ?, ?);");
			statement.setString(1, id);
			statement.setString(2, "MyMaid_2019PumpkinID");
			statement.setString(3, player.getName());
			statement.setString(4, player.getUniqueId().toString());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			ErrorReporter.report(e);
			return;
		}
		player.sendMessage("[2019Pumpkin] " + ChatColor.GREEN + "Happy Halloween!");
		if (player.getInventory().firstEmpty() == -1) {
			player.getLocation().getWorld().dropItem(player.getLocation(), item);
			player.sendMessage("[2019Pumpkin] " + ChatColor.GREEN + "インベントリがいっぱいだったため、あなたの足元にアイテムをドロップしました。");
			Bukkit.getLogger().info("[2019Pumpkin] dropped to " + player.getName());
		} else {
			player.getInventory().addItem(item);
			Bukkit.getLogger().info("[2019Pumpkin] gived to " + player.getName());
		}
	}
}
