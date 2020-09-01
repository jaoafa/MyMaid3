package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.Historyjao;
import com.jaoafa.MyMaid3.Lib.Historyjao.HistoryData;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class Event_History extends MyMaidLibrary implements Listener {
    @EventHandler
    public void OnEvent_JoinHistory(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Historyjao histjao = new Historyjao(player);

        if (!histjao.isFound()) {
            return;
        }

        if (histjao.getHistoryDatas().isEmpty()) {
            return;
        }

        List<String> data = new ArrayList<>();
        for (HistoryData hist : histjao.getHistoryDatas()) {
            if (hist.disabled) {
                return;
            }
            data.add("[" + hist.id + "] " + hist.message + " - " + sdfFormat(hist.getCreatedAt()));
        }

        if (data.isEmpty()) {
            return;
        }

        MyMaidConfig.getJDA().getTextChannelById(597423444501463040L)
                .sendMessage("**-----: Historyjao DATA / `" + player.getName() + "` :-----**\n"
                        + "```" + String.join("\n", data) + "```")
                .queue();
    }
}
