package com.jaoafa.MyMaid3.Command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;

public class Cmd_Help extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 1 && args[0].equalsIgnoreCase("regist")) {
			// /help regist
			// AM only
			String group = PermissionsManager.getPermissionMainGroup(player);
			if (!group.equalsIgnoreCase("Admin") && !group.equalsIgnoreCase("Moderator")) {
				// AM以外
				SendMessage(sender, cmd, "registはあなたの権限では利用できません。");
				return true;
			}
			ItemStack item = player.getInventory().getItemInMainHand();
			if (item.getType() != Material.WRITTEN_BOOK) {
				SendMessage(sender, cmd, "このコマンドを使用するには、送る本を手に持ってください。");
				return true;
			}
			BookMeta book = (BookMeta) player.getInventory().getItemInMainHand().getItemMeta();
			List<String> lore = book.hasLore() ? book.getLore() : null;
			if (!book.hasPages()) {
				SendMessage(sender, cmd, "エラーが発生しました。詳しくはプラグイン制作者にお問い合わせください。Debug: Pages null");
				return true;
			}
			List<String> pages = book.getPages();
			boolean res = save(lore, pages);
			if (res) {
				SendMessage(sender, cmd, "登録に成功しました。");
			} else {
				SendMessage(sender, cmd, "登録に失敗しました…");
			}
			return true;
		}
		ItemStack is = load();
		if (is == null) {
			SendMessage(sender, cmd, "現在このコマンドは動作しません。");
			return true;
		}
		if (player.getInventory().firstEmpty() == -1) {
			player.getLocation().getWorld().dropItem(player.getLocation(), is);
			SendMessage(sender, cmd, "ヘルプブックをインベントリに追加しようとしましたが、インベントリが一杯だったのであなたの足元にドロップしました。");
		} else {
			player.getInventory().addItem(is);
			SendMessage(sender, cmd, "ヘルプブックをインベントリに追加しました。");
		}
		return true;
	}

	boolean save(List<String> lore, List<String> pages) {
		File file = new File(Main.getJavaPlugin().getDataFolder(), "helpBook.yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		if (data.contains("lore")) {
			data.set("lore_bak" + System.currentTimeMillis() / 1000, data.getStringList("lore"));
		}
		data.set("lore", lore);
		if (data.contains("pages")) {
			data.set("pages_bak" + System.currentTimeMillis() / 1000, data.getStringList("pages"));
		}
		data.set("pages", pages);
		try {
			data.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	ItemStack load() {
		File file = new File(Main.getJavaPlugin().getDataFolder(), "helpBook.yml");
		if (!file.exists()) {
			return null;
		}
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);

		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setTitle("jao Minecraft Server Help book");
		bookMeta.setAuthor("jaotan");
		if (data.contains("lore")) {
			bookMeta.setLore(data.getStringList("lore"));
		}
		if (!data.contains("pages")) {
			return null;
		}
		bookMeta.setPages(data.getStringList("pages"));
		book.setItemMeta(bookMeta);
		return book;
	}

	@Override
	public String getDescription() {
		return "ヘルプブックを入手します。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/help: ヘルプブックを入手します。");
				add("/help regist: ヘルプブックを登録します。運営のみ使用できます。");
			}
		};
	}
}
