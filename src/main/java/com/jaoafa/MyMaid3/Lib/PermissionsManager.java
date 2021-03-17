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

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionsManager {
    /**
     * 指定されたプレイヤーの権限グループリストを取得します。
     *
     * @param player プレイヤー名
     * @return プレイヤーが居る権限グループリスト
     */
    public static List<String> getPermissionGroupList(OfflinePlayer player) {
        LuckPerms LPApi = LuckPermsProvider.get();
        User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
        if (LPplayer == null) {
            throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
        }
        List<String> list = new ArrayList<>();
        String groupname = LPplayer.getPrimaryGroup();
        Group group = LPApi.getGroupManager().getGroup(groupname);
        if (group == null)
            throw new InternalError("Groupがnullです。");
        list.add(group.getFriendlyName());
        return list;
    }

    /**
     * 指定されたプレイヤーの権限グループリストを取得します。
     *
     * @param player プレイヤー
     * @return プレイヤーが居る権限グループリスト
     */
    public static List<String> getPermissionGroupList(Player player) {
        LuckPerms LPApi = LuckPermsProvider.get();
        User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
        if (LPplayer == null) {
            throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
        }
        List<String> list = new ArrayList<>();
        String groupname = LPplayer.getPrimaryGroup();
        Group group = LPApi.getGroupManager().getGroup(groupname);
        if (group == null)
            throw new InternalError("Groupがnullです。");
        list.add(group.getFriendlyName());
        return list;
    }

    public static String getPermissionMainGroup(String player)
            throws UnsupportedOperationException, IllegalArgumentException {
        @SuppressWarnings("deprecation")
        OfflinePlayer offplayer = Bukkit.getOfflinePlayer(player);
        return getPermissionMainGroup(offplayer);
    }

    public static String getPermissionMainGroup(UUID uuid)
            throws UnsupportedOperationException, IllegalArgumentException {
        OfflinePlayer offplayer = Bukkit.getOfflinePlayer(uuid);
        return getPermissionMainGroup(offplayer);
    }

    /**
     * 指定されたプレイヤーのメイン権限グループを取得します。
     *
     * @param player プレイヤー名
     * @return メイン権限グループ名
     */
    public static String getPermissionMainGroup(OfflinePlayer player) {
        LuckPerms LPApi = LuckPermsProvider.get();
        User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
        if (LPplayer == null) {
            throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
        }
        String groupname = LPplayer.getPrimaryGroup();
        Group group = LPApi.getGroupManager().getGroup(groupname);
        if (group == null)
            throw new InternalError("Groupがnullです。");
        return group.getFriendlyName();
    }

    /**
     * 指定されたプレイヤーのメイン権限グループを取得します。
     *
     * @param player プレイヤー
     * @return メイン権限グループ名
     */
    public static String getPermissionMainGroup(Player player) {
        LuckPerms LPApi = LuckPermsProvider.get();
        User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
        if (LPplayer == null) {
            throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
        }
        String groupname = LPplayer.getPrimaryGroup();
        Group group = LPApi.getGroupManager().getGroup(groupname);
        if (group == null)
            throw new InternalError("Groupがnullです。");
        return group.getFriendlyName();
    }

    public static void setPermissionsGroup(Player player, String groupname) {
        LuckPerms LPApi = LuckPermsProvider.get();
        User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
        if (LPplayer == null) {
            throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
        }
        Group group = LPApi.getGroupManager().getGroup(groupname);
        if (group == null) {
            throw new InternalError("Groupがnullです。");
        }

        DataMutateResult result = LPplayer.setPrimaryGroup(group.getName());

        if (!result.wasSuccessful()) {
            System.out.println("setPermissionsGroup: " + result.name());
            boolean bool = Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "lp user " + player.getUniqueId().toString() + " parent set " + group.getName());
            if (bool) {
                System.out.println("setPermissionsGroup lp command: Successful");
            } else {
                System.out.println("setPermissionsGroup lp command: Failed");
            }
        }

        LPApi.getUserManager().saveUser(LPplayer);
    }

    public static void setPermissionsGroup(String uuidstr, String groupname) {
        LuckPerms LPApi = LuckPermsProvider.get();
        User LPplayer = null;
        try {
            UUID uuid = UUID.fromString(uuidstr);
            LPplayer = LPApi.getUserManager().getUser(uuid);
        } catch (IllegalArgumentException ignored) {
        }
        if (LPplayer == null) {
            LPplayer = LPApi.getUserManager().getUser(uuidstr);
            if (LPplayer == null) {
                throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
            }
        }
        Group group = LPApi.getGroupManager().getGroup(groupname);
        if (group == null) {
            throw new InternalError("Groupがnullです。");
        }

        DataMutateResult result = LPplayer.setPrimaryGroup(group.getName());

        if (!result.wasSuccessful()) {
            System.out.println("setPermissionsGroup: " + result.name());
            boolean bool = Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "lp user " + uuidstr + " parent set " + group.getName());
            if (bool) {
                System.out.println("setPermissionsGroup lp command: Successful");
            } else {
                System.out.println("setPermissionsGroup lp command: Failed");
            }
        }

        LPApi.getUserManager().saveUser(LPplayer);
    }
}
