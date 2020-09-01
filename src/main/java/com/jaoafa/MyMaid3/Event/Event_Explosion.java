package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

public class Event_Explosion extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onHangingDamageByTNT(HangingBreakEvent event) {
        if (event.getCause() != RemoveCause.EXPLOSION) {
            return;
        }
        event.setCancelled(true);
    }
}
