package com.jaoafa.MyMaid3.Event;

import java.awt.Color;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import com.jaoafa.MyMaid3.Main;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class Event_PacketLimiterKickNotify extends MyMaidLibrary implements Listener {
	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		if (event.getReason().equalsIgnoreCase("You are sending too many packets!") ||
				event.getReason().equalsIgnoreCase("You are sending too many packets, :(")) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.withTitle("警告！！");
			embed.withDescription("プレイヤーがパケットを送信しすぎてKickされました。ハッククライアントの可能性があります。");
			embed.appendField("Reason", event.getReason(), false);
			embed.withAuthorName(event.getPlayer().getName());
			embed.withAuthorUrl("https://jaoafa.com/user/" + event.getPlayer().getUniqueId().toString());
			embed.withAuthorIcon("https://crafatar.com/avatars/" + event.getPlayer().getUniqueId().toString());
			embed.withColor(Color.ORANGE);
			embed.appendField("プレイヤー", event.getPlayer().getName(), true);
			embed.appendField("理由", event.getReason(), false);
			IDiscordClient client = Main.getDiscordClient();
			RequestBuffer.request(() -> {
				try {
					client.getChannelByID(597423444501463040L).sendMessage(embed.build());
				} catch (DiscordException discordexception) {
					Main.DiscordExceptionError(getClass(), null, discordexception);
				}
			});
		}
	}
}
