package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jaoafa.MyMaid3.Lib.AFKPlayer;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

public class Cmd_AFK extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (args.length == 1) {
			Player player = Bukkit.getPlayerExact(args[0]);
			if (player == null) {
				SendMessage(sender, cmd, "指定されたプレイヤー「" + args[0] + "」は見つかりませんでした。");

				Player any_chance_player = Bukkit.getPlayer(args[0]);
				if (any_chance_player != null) {
					SendMessage(sender, cmd, "もしかして: " + any_chance_player.getName());
				}
				return true;
			}
			AFKPlayer afkplayer = new AFKPlayer(player);
			if (afkplayer.isAFK()) {
				SendMessage(sender, cmd, "指定されたプレイヤー「" + player.getName() + "」は現在AFKです。");
				long sa = afkplayer.getAFKingSec();
				StringBuilder builder = new StringBuilder();

				int year = (int) (sa / 31536000L);
				int year_remain = (int) (sa % 31536000L);
				if (year != 0) {
					builder.append(year + "年");
				}
				int month = (int) (year_remain / 2592000L);
				int month_remain = (int) (year_remain % 2592000L);
				if (month != 0) {
					builder.append(month + "か月");
				}
				int day = (int) (month_remain / 86400L);
				int day_remain = (int) (month_remain % 86400L);
				if (day != 0) {
					builder.append(day + "日");
				}
				int hour = (int) (day_remain / 3600L);
				int hour_remain = (int) (day_remain % 3600L);
				if (hour != 0) {
					builder.append(hour + "時間");
				}
				int minute = (int) (hour_remain / 60L);
				if (minute != 0) {
					builder.append(minute + "分");
				}
				int sec = (int) (hour_remain % 60L);
				if (sec != 0) {
					builder.append(sec + "秒");
				}
				String startTime = MyMaidLibrary.sdfFormat(new Date(afkplayer.getAFKStartTime() * 1000));
				SendMessage(sender, cmd, "AFK開始時刻: " + startTime);
				SendMessage(sender, cmd, "AFK経過時間: " + builder.toString());
			} else {
				SendMessage(sender, cmd, "指定されたプレイヤー「" + player.getName() + "」は現在AFKではありません。");
			}
			return true;
		}
		if (!(sender instanceof Player)) {
			SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
			return true;
		}
		Player player = (Player) sender;
		AFKPlayer afkplayer = new AFKPlayer(player);
		if (!afkplayer.isAFK()) {
			afkplayer.start();
		} else {
			afkplayer.end();
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "AFK(Away From Keyboard)モードをオン・オフにします。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/afk: AFK(Away From Keyboard)モードをオン・オフにします。(トグル)");
				add("/afk <Player>: 指定したPlayerのAFK情報を表示します。");
			}
		};
	}
}
