package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
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
			if (!Main.coOLDLoc.containsKey(player.getUniqueId())) {
				SendMessage(sender, cmd, "先に/coold iをしてブロック情報を取得してからページ処理してください。");
				return true;
			}
			Location loc = Main.coOLDLoc.get(player.getUniqueId());
			BukkitTask task = new Task_CoOLD(player, loc, 1).runTaskAsynchronously(Main.getJavaPlugin());
			Main.coOLDEnabler.put(player.getUniqueId(), task);
		}

		if (!Main.coOLDEnabler.containsKey(player.getUniqueId())) {
			Main.coOLDEnabler.put(player.getUniqueId(), null);

			SendMessage(sender, cmd, "CP OLD - Inspector now enabled.");
		} else {
			if (Main.coOLDEnabler.get(player.getUniqueId()) != null
					&& !Main.coOLDEnabler.get(player.getUniqueId()).isCancelled()) {
				Main.coOLDEnabler.get(player.getUniqueId()).cancel();
			}
			Main.coOLDEnabler.remove(player.getUniqueId());

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
