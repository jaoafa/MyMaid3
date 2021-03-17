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

package com.jaoafa.MyMaid3.Lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class TPSChecker {
    private final static String name = Bukkit.getServer().getClass().getPackage().getName();
    private final static String version = name.substring(name.lastIndexOf('.') + 1);
    private static Object serverInstance;
    private static Field tpsField;

    /**
     * TPSを取得する(1m)
     *
     * @return tps
     * @author mine_book000
     */
    public static String getTPS1m() {
        try {
            double[] tpsdouble = ((double[]) tpsField.get(serverInstance));
            if (tpsdouble[0] > 20.0) {
                return "*" + Math.min(Math.round(tpsdouble[0] * 100.0) / 100.0, 20.0);
            }
            return "" + Math.min(Math.round(tpsdouble[0] * 100.0) / 100.0, 20.0);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    //private final static DecimalFormat format = new DecimalFormat("##.##");

    /**
     * TPSを取得する(5m)
     *
     * @return tps
     * @author mine_book000
     */
    public static String getTPS5m() {
        try {
            double[] tpsdouble = ((double[]) tpsField.get(serverInstance));
            if (tpsdouble[1] > 20.0) {
                return "*" + Math.min(Math.round(tpsdouble[1] * 100.0) / 100.0, 20.0);
            }
            return "" + Math.min(Math.round(tpsdouble[1] * 100.0) / 100.0, 20.0);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TPSを取得する(15m)
     *
     * @return tps
     * @author mine_book000
     */
    public static String getTPS15m() {
        try {
            double[] tpsdouble = ((double[]) tpsField.get(serverInstance));
            if (tpsdouble[2] > 20.0) {
                return "*" + Math.min(Math.round(tpsdouble[2] * 100.0) / 100.0, 20.0);
            }
            return "" + Math.min(Math.round(tpsdouble[2] * 100.0) / 100.0, 20.0);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChatColor TPSColor(double tps) {
        if (tps > 18.0) {
            return ChatColor.GREEN;
        }
        if (tps > 16.0) {
            return ChatColor.YELLOW;
        }
        return ChatColor.RED;
    }

    public static void OnEnable_TPSSetting() {
        try {
            serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
            tpsField = serverInstance.getClass().getField("recentTps");
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
