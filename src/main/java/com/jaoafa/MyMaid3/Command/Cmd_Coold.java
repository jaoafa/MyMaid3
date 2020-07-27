package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Task.Task_CoOLD;

public class Cmd_Coold extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (!(sender instanceof Player)) {
			SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
			return true;
		}
		Player player = (Player) sender;
		if (!isAMR(player)) {
			SendMessage(sender, cmd, "あなたはこのコマンドを使用できません。");
			return true;
		}

		if (args.length >= 2 && args[0].equalsIgnoreCase("l")) {
			// /coold l <PAGE>
			if (!MyMaidConfig.getCoOLDLoc().containsKey(player.getUniqueId())) {
				SendMessage(sender, cmd, "先に/coold iをしてブロック情報を取得してからページ処理してください。");
				return true;
			}
			if (!isInt(args[1]) || Integer.valueOf(args[1]) <= 0) {
				SendMessage(sender, cmd, "有効なページを指定してください。");
				return true;
			}

			player.sendMessage("[CoreProtectOLD] " + ChatColor.LIGHT_PURPLE + "Please wait...");
			Location loc = MyMaidConfig.getCoOLDLoc().get(player.getUniqueId());
			BukkitTask task = new Task_CoOLD(player, loc, Integer.valueOf(args[1]))
					.runTaskAsynchronously(Main.getJavaPlugin());
			MyMaidConfig.putCoOLDEnabler(player.getUniqueId(), task);
			return true;
		}

		if (!MyMaidConfig.getCoOLDEnabler().containsKey(player.getUniqueId())) {
			MyMaidConfig.putCoOLDEnabler(player.getUniqueId(), null);

			SendMessage(sender, cmd, "CP OLD - Inspector now enabled.");
		} else {
			if (MyMaidConfig.getCoOLDEnabler().get(player.getUniqueId()) != null
					&& !MyMaidConfig.getCoOLDEnabler().get(player.getUniqueId()).isCancelled()) {
				MyMaidConfig.getCoOLDEnabler().get(player.getUniqueId()).cancel();
			}
			MyMaidConfig.removeCoOLDEnabler(player.getUniqueId());

			SendMessage(sender, cmd, "CP OLD - Inspector now disabled.");
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "旧CoreProtectの情報を取得します。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/coold i");
				add("/coold l <Page>");
			}
		};
	}
}
