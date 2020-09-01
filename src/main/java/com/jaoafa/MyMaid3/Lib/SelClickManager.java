package com.jaoafa.MyMaid3.Lib;

import com.jaoafa.MyMaid3.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelClickManager {
    static Map<UUID, Boolean> cache = new HashMap<>();
    static File file = new File(Main.getJavaPlugin().getDataFolder(), "selclick.yml");

    public static boolean isEnable(Player player) {
        if (cache.containsKey(player.getUniqueId())) {
            return cache.get(player.getUniqueId());
        } else {
            FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
            if (conf.contains(player.getUniqueId().toString())) {
                cache.put(player.getUniqueId(), conf.getBoolean(player.getUniqueId().toString()));
                return conf.getBoolean(player.getUniqueId().toString());
            } else {
                cache.put(player.getUniqueId(), true);
                return true; // default value
            }
        }
    }

    public static void setStatus(Player player, boolean newValue) {
        cache.put(player.getUniqueId(), newValue);
        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
        conf.set(player.getUniqueId().toString(), newValue);
        try {
            conf.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
