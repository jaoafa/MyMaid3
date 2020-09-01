package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Command.Cmd_TempMute;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Event_TempMute extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (Cmd_TempMute.tempmutes.size() == 0) {
            return;
        }
        event.getRecipients().removeAll(Cmd_TempMute.tempmutes);
    }
}
