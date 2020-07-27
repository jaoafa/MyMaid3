package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

public class Cmd_Feedback extends MyMaidLibrary implements CommandExecutor, CommandPremise {
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
		String message = String.join(" ", args) + "\nby `" + sender.getName() + "`";

		WebhookClientBuilder builder = new WebhookClientBuilder(MyMaidConfig.getFeedbackWebhookUrl());
		builder.setThreadFactory((job) -> {
			Thread thread = new Thread(job);
			thread.setName("MyMaid3Feedback");
			thread.setDaemon(true);
			return thread;
		});
		builder.setWait(true);
		WebhookClient client = builder.build();
		WebhookMessageBuilder msgbuilder = new WebhookMessageBuilder();
		msgbuilder.setUsername("MyMaid3 Feedback");
		msgbuilder.setContent(message);
		sender.sendMessage("[Feedback] " + ChatColor.GREEN + "フィードバックを送信しています…");
		client.send(msgbuilder.build()).whenComplete((msg, ex) -> {
			if (ex == null) {
				// 成功した場合
				sender.sendMessage("[Feedback] " + ChatColor.GREEN + "フィードバックを送信しました。ありがとうございます！");
			} else {
				// 失敗した場合
				sender.sendMessage("[Feedback] " + ChatColor.GREEN + "大変申し訳ございません、フィードバックを送信できませんでした。");
				sender.sendMessage("[Feedback] " + ChatColor.GREEN + "少し時間をおいてから再度実行するか、別の方法を用いて運営までお問い合わせください。");
			}
		});
		return true;
	}

	@Override
	public String getDescription() {
		return "フィードバックを行います。メッセージは運営チャンネルに送られます。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/feedback <Message>");
			}
		};
	}
}
