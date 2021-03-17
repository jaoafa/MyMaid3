package com.jaoafa.MyMaid3.Task;

import com.jaoafa.jaoSuperAchievement2.API.AchievementAPI;
import com.jaoafa.jaoSuperAchievement2.API.Achievementjao;
import com.jaoafa.jaoSuperAchievement2.Lib.AchievementType;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Task_NewStep extends BukkitRunnable {
    final Set<UUID> getted = new HashSet<>();

    public Task_NewStep() {
    }

    public static boolean isBakushinchi(Location loc) {
        if (!loc.getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
            return false; // Jao_Afa以外では適用しない
        }

        WorldGuardPlugin wg = getWorldGuard();
        if (wg == null) {
            return false;
        }
        RegionManager rm = wg.getRegionManager(loc.getWorld());
        ApplicableRegionSet regions = rm.getApplicableRegions(loc);
        if (regions.size() == 0) {
            return false;
        }
        List<ProtectedRegion> inheritance = new LinkedList<>();
        for (ProtectedRegion region : regions) {
            inheritance.add(region);
        }
        Collections.reverse(inheritance);
        ProtectedRegion firstregion = inheritance.get(0);
        return firstregion.getId().equalsIgnoreCase("Bakushinchi");
    }

    private static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getted.contains(player.getUniqueId())) {
                continue;
            }
            Location loc = player.getLocation();
            if (isBakushinchi(loc)) {
                continue;
            }
            if (!Achievementjao.getAchievement(player, new AchievementType(49))) {
                player.sendMessage(AchievementAPI.getPrefix() + "実績の解除中に問題が発生しました。もう一度お試しください。");
                return;
            }
            getted.add(player.getUniqueId());
        }
    }
}
