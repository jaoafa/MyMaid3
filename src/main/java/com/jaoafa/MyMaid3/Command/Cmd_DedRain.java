package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Cmd_DedRain extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (args.length == 0) {
			// /dedrain
			String dedrainStr = MyMaidConfig.isDedRaining() ? "オン" : "オフ";
			SendMessage(sender, cmd, "現在のDedRain設定は「" + dedrainStr + "」です。");
			return true;
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("on")) {
				// /dedrain on
				MyMaidConfig.setDedRaining(true);
				SendMessage(sender, cmd, "現在のDedRain設定は「オン」です。");
				return true;
			} else if (args[0].equalsIgnoreCase("off")) {
				// /dedrain off
				MyMaidConfig.setDedRaining(false);
				SendMessage(sender, cmd, "現在のDedRain設定は「オフ」です。");
				return true;
			}
		}
		SendUsageMessage(sender, cmd);
		return true;
	}

	@Override
	public String getDescription() {
		return "雨を降らせたり降らせなかったりします。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/dedrain: 現在の設定状態を表示します。");
				add("/dedrain on: 雨を降らせないようにします。(デフォルト)");
				add("/dedrain off: 雨を降らせれるようにします。10分で自動的に無効化されます。");
			}
		};
	}
}
