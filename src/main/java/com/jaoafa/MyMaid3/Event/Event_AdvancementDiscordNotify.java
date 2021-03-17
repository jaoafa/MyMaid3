/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Event_AdvancementDiscordNotify extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onDone(PlayerAdvancementDoneEvent event) {
        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                Advancement advancement = event.getAdvancement();
                String key = advancement.getKey().getKey().replace("/", ".");
                String namespace = advancement.getKey().getNamespace();

                if (!namespace.equals("minecraft")) {
                    return;
                }

                InputStream is = getClass().getResourceAsStream("/advancements_ja.json");
                if (is == null) {
                    return;
                }
                JSONObject obj;
                try {
                    String str = toString(is);
                    obj = new JSONObject(str);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return;
                }

                if (!obj.has(key)) {
                    return;
                }
                EmbedBuilder builder = new EmbedBuilder();
                String url = "https://jaoafa.com/user/{uuid}"
                        .replace("{uuid}", player.getUniqueId().toString());
                String iconUrl = "https://minotar.net/helm/{uuid}/{size}"
                        .replace("{uuid}", player.getUniqueId().toString().replace("-", ""))
                        .replace("{size}", "128");
                builder.setAuthor(player.getName() + " has made the advancement " + obj.getString(key), url, iconUrl);
                builder.setColor(Color.PINK);

                MyMaidConfig.getServerChatChannel().sendMessage(builder.build()).queue();
            }

            String toString(InputStream is) throws IOException {
                InputStreamReader reader = new InputStreamReader(is);
                StringBuilder builder = new StringBuilder();
                char[] buf = new char[1024];
                int numRead;
                while (0 <= (numRead = reader.read(buf))) {
                    builder.append(buf, 0, numRead);
                }
                return builder.toString();
            }
        }.runTaskAsynchronously(Main.getJavaPlugin());
    }
}
