package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
			if (args[0].equalsIgnoreCase("data")) {
				Map<String, AFKPlayer> list = AFKPlayer.getAFKPlayers();
				SendMessage(sender, cmd, "データ数: " + list.size());
				for (Entry<String, AFKPlayer> player : list.entrySet()) {
					SendMessage(sender, cmd,
							player.getKey() + " | afking: " + Boolean.toString(player.getValue().isAFK()));
				}
				return true;
			}
			Player player = Bukkit.getPlayerExact(args[0]);
			if (player == null) {
				if (!(sender instanceof Player)) {
					SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
					return true;
				}
				AFKPlayer afkplayer = new AFKPlayer((Player) sender);
				String message = String.join(" ", args);
				if (!afkplayer.isAFK()) {
					afkplayer.start(message);
				} else {
					SendMessage(sender, cmd, "あなたは現在AFK状態です。AFKにメッセージを設定するには、一度AFKを解除してください。");
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
				if (afkplayer.getLastActionTime() != -1L) {
					String actionTime = MyMaidLibrary.sdfFormat(new Date(afkplayer.getLastActionTime() * 1000));
					SendMessage(sender, cmd, "このプレイヤーの最終アクションは " + actionTime + " です。");
					if (afkplayer.getMessage() != null) {
						SendMessage(sender, cmd, "プレイヤーが設定したAFKメッセージ: " + afkplayer.getMessage());
					}
				}
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
				add("/afk <Message>: AFK(Away From Keyboard)モードをメッセージ付きでオンにします。");
				add("/afk <Player>: 指定したPlayerのAFK情報を表示します。");
			}
		};
	}
}
