package com.jaoafa.MyMaid3.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Cmd_Bug extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	static Map<UUID, String> bugreports = new HashMap<>();
	static Map<UUID, Long> bugreporttime = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (args.length == 0) {
			SendUsageMessage(sender, cmd);
			return true;
		}
		if (!(sender instanceof Player)) {
			SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
			return true;
		}
		Player player = (Player) sender;

		if (args[0].equalsIgnoreCase("true")) {
			if (!bugreports.containsKey(player.getUniqueId())) {
				// ない
				player.sendMessage("[Bug] " + ChatColor.GREEN + "送信できるメッセージがありません。まず/bug <Message>を実行してください。");
				return true;
			}
			sendMessage(bugreports.get(player.getUniqueId()), player);
			bugreports.remove(player.getUniqueId());
			return true;
		} else if (args[0].equalsIgnoreCase("false")) {
			if (bugreports.containsKey(player.getUniqueId())) {
				bugreports.remove(player.getUniqueId());
			}
			player.sendMessage("[Bug] " + ChatColor.GREEN + "送信予定メッセージを削除しました。");
			return true;
		}

		player.sendMessage("[Bug] " + ChatColor.RED + "送信前に再度次の内容をご確認ください！");
		player.sendMessage("[Bug] " + ChatColor.GREEN + "送信予定メッセージ: " + String.join(" ", args));
		player.sendMessage("[Bug] " + ChatColor.GREEN + "・公式Discordサーバの#develop_todoを確認してください。");
		player.sendMessage("[Bug] " + ChatColor.GREEN + "  既に同様の報告がなされていませんか？");
		player.sendMessage("[Bug] " + ChatColor.GREEN + "・5W1Hを用いてわかりやすく説明されていますか？");
		player.sendMessage("[Bug] " + ChatColor.GREEN + "  特に「どこで」「どうすると」バグが起こるかを詳しく記載してください。");

		String nowVer = Main.getJavaPlugin().getDescription().getVersion();
		String nowVerSha = getVersionSha(nowVer);
		String latestVerSha = getLastCommitSha("MyMaid3");
		if (!nowVerSha.equalsIgnoreCase(latestVerSha)) {
			player.sendMessage("[Bug] " + ChatColor.GREEN + "・MyMaid3の現在導入済みバージョン(" + nowVerSha + ")と最新リリースバージョン("
					+ latestVerSha + ")が異なります。");
			player.sendMessage("[Bug] " + ChatColor.GREEN + "  最近更新された事項でないかどうか確認してください。");
		}
		player.sendMessage("[Bug] " + ChatColor.GREEN + "");

		BaseComponent[] hover1Component = TextComponent.fromLegacyText("「/bug true」を実行します。");

		ClickEvent clickEvent_true = new ClickEvent(Action.RUN_COMMAND, "/bug true");
		HoverEvent hover1Event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover1Component);

		TextComponent main1Component = new TextComponent();
		main1Component.setText("[Bug] ");

		TextComponent first1Component = new TextComponent();
		first1Component.setText("上記内容を確認し、送信する場合は ");
		first1Component.setColor(net.md_5.bungee.api.ChatColor.GREEN);
		main1Component.addExtra(first1Component);

		TextComponent yes1Component = new TextComponent();
		yes1Component.setText("ここ");
		yes1Component.setClickEvent(clickEvent_true);
		yes1Component.setColor(net.md_5.bungee.api.ChatColor.AQUA);
		yes1Component.setHoverEvent(hover1Event);
		yes1Component.setUnderlined(true);
		main1Component.addExtra(yes1Component);

		TextComponent end1Component = new TextComponent();
		end1Component.setText(" をクリックしてください。");
		end1Component.setColor(net.md_5.bungee.api.ChatColor.GREEN);
		main1Component.addExtra(end1Component);

		player.spigot().sendMessage(main1Component);

		// ----- //

		BaseComponent[] hover2Component = TextComponent.fromLegacyText("「/bug false」を実行します。");

		ClickEvent clickEvent_false = new ClickEvent(Action.RUN_COMMAND, "/bug false");
		HoverEvent hover2Event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover2Component);

		TextComponent main2Component = new TextComponent();
		main2Component.setText("[Bug] ");

		TextComponent first2Component = new TextComponent();
		first2Component.setText("送信しない場合は ");
		first2Component.setColor(net.md_5.bungee.api.ChatColor.GREEN);
		main2Component.addExtra(first2Component);

		TextComponent yes2Component = new TextComponent();
		yes2Component.setText("ここ");
		yes2Component.setClickEvent(clickEvent_false);
		yes2Component.setColor(net.md_5.bungee.api.ChatColor.AQUA);
		yes2Component.setHoverEvent(hover2Event);
		yes2Component.setUnderlined(true);
		main2Component.addExtra(yes2Component);

		TextComponent end2Component = new TextComponent();
		end2Component.setText(" をクリックしてください。");
		end2Component.setColor(net.md_5.bungee.api.ChatColor.GREEN);
		main2Component.addExtra(end2Component);

		player.spigot().sendMessage(main2Component);

		bugreports.put(player.getUniqueId(), String.join(" ", args));
		return true;
	}

	private void sendMessage(String mesg, Player player) {
		String message = mesg + "\nby `" + player.getName() + "`";

		WebhookClientBuilder builder = new WebhookClientBuilder(Main.BugReportWebhookUrl);
		builder.setThreadFactory((job) -> {
			Thread thread = new Thread(job);
			thread.setName("MyMaid3BugReporter");
			thread.setDaemon(true);
			return thread;
		});
		builder.setWait(true);
		WebhookClient client = builder.build();
		WebhookMessageBuilder msgbuilder = new WebhookMessageBuilder();
		msgbuilder.setUsername("MyMaid3 BugReporter");
		msgbuilder.setContent(message);
		player.sendMessage("[Bug] " + ChatColor.GREEN + "バグ報告を送信しています…");
		client.send(msgbuilder.build()).whenComplete((msg, ex) -> {
			if (ex == null) {
				// 成功した場合
				player.sendMessage("[Bug] " + ChatColor.GREEN + "バグ報告を送信しました。ありがとうございます！");
			} else {
				// 失敗した場合
				player.sendMessage("[Bug] " + ChatColor.GREEN + "大変申し訳ございません、バグ報告を送信できませんでした。");
				player.sendMessage("[Bug] " + ChatColor.GREEN + "少し時間をおいてから再度実行するか、別の方法を用いて運営までお問い合わせください。");
			}
		});
	}

	private String getLastCommitSha(String repo) {
		try {
			String url = "https://api.github.com/repos/jaoafa/" + repo + "/commits";
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(url).get().build();
			Response response = client.newCall(request).execute();
			if (response.code() != 200) {
				return null;
			}
			JSONArray array = new JSONArray(response.body().string());
			response.close();

			return array.getJSONObject(0).getString("sha").substring(0, 7);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getVersionSha(String version) {
		String[] day_time = version.split("_");
		if (day_time.length == 3) {
			return day_time[2];
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "バグ報告を行います。メッセージは公式Discordサーバの#develop_todoに送られます。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/bug <Message>");
			}
		};
	}
}
