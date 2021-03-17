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
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event_CPDefectSearcher extends MyMaidLibrary implements Listener {
    private static void convertByteData(List<Object> data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(data);
        oos.flush();
        oos.close();
        bos.close();
        bos.toByteArray();
    }

    private static List<Object> processMeta(BlockState block) {
        List<Object> meta = new ArrayList<>();
        if (block instanceof CommandBlock) {
            CommandBlock command_block = (CommandBlock) block;
            String command = command_block.getCommand();
            if (command.length() > 0) meta.add(command);
        } else if (block instanceof Banner) {
            Banner banner = (Banner) block;
            meta.add(banner.getBaseColor());
            List<Pattern> patterns = banner.getPatterns();
            for (Pattern pattern : patterns) meta.add(pattern.serialize());
        } else if (block instanceof ShulkerBox) {
            ShulkerBox shulkerBox = (ShulkerBox) block;
            ItemStack[] inventory = shulkerBox.getInventory().getStorageContents();
            int slot = 0;
            for (ItemStack itemStack : inventory) {
                if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                    Map<Integer, Object> itemMap = new HashMap<>();
                    ItemStack item = itemStack.clone();
                    List<List<Map<String, Object>>> metadata = getItemMeta(item, item.getType(), slot);
                    item.setItemMeta(null);
                    itemMap.put(0, item.serialize());
                    itemMap.put(1, metadata);
                    meta.add(itemMap);
                }
                slot++;
            }

        }
        if (meta.size() == 0) meta = null;
        return meta;
    }

    private static List<List<Map<String, Object>>> getItemMeta(ItemStack i, Material type, int slot) {
        List<List<Map<String, Object>>> metadata = new ArrayList<>();
        List<Map<String, Object>> list = new ArrayList<>();
        if (i.hasItemMeta() && i.getItemMeta() != null) if (i.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta().clone();
            LeatherArmorMeta sub_meta = meta.clone();
            meta.setColor(null);
            list.add(meta.serialize());
            metadata.add(list);
            list = new ArrayList<>();
            list.add(sub_meta.getColor().serialize());
            metadata.add(list);
        } else if (i.getItemMeta() instanceof FireworkMeta) {
            FireworkMeta meta = (FireworkMeta) i.getItemMeta().clone();
            FireworkMeta sub_meta = meta.clone();
            meta.clearEffects();
            list.add(meta.serialize());
            metadata.add(list);
            if (sub_meta.hasEffects())
                for (FireworkEffect effect : sub_meta.getEffects()) getFireworkEffect(effect, metadata);
        } else if (i.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) i.getItemMeta().clone();
            PotionMeta sub_meta = meta.clone();
            meta.clearCustomEffects();
            list.add(meta.serialize());
            metadata.add(list);
            if (sub_meta.hasCustomEffects()) for (PotionEffect effect : sub_meta.getCustomEffects()) {
                list = new ArrayList<>();
                list.add(effect.serialize());
                metadata.add(list);
            }
        } else if (i.getItemMeta() instanceof FireworkEffectMeta) {
            FireworkEffectMeta meta = (FireworkEffectMeta) i.getItemMeta().clone();
            FireworkEffectMeta sub_meta = meta.clone();
            meta.setEffect(null);
            list.add(meta.serialize());
            metadata.add(list);
            if (sub_meta.hasEffect()) {
                FireworkEffect effect = sub_meta.getEffect();
                getFireworkEffect(effect, metadata);
            }

        } else if (i.getItemMeta() instanceof BannerMeta) {
            BannerMeta meta = (BannerMeta) i.getItemMeta().clone();
            BannerMeta sub_meta = (BannerMeta) meta.clone();
            meta.setPatterns(new ArrayList<>());
            list.add(meta.serialize());
            metadata.add(list);
            for (Pattern pattern : sub_meta.getPatterns()) {
                list = new ArrayList<>();
                list.add(pattern.serialize());
                metadata.add(list);
            }

        } else if (i.getItemMeta() instanceof MapMeta) {
            MapMeta meta = (MapMeta) i.getItemMeta().clone();
            MapMeta sub_meta = meta.clone();
            meta.setColor(null);
            list.add(meta.serialize());
            metadata.add(list);
            list = new ArrayList<>();
            list.add(sub_meta.getColor().serialize());
            metadata.add(list);
        } else {

            ItemMeta meta = i.getItemMeta().clone();
            list.add(meta.serialize());
            metadata.add(list);
        }
        if (type != null && type.equals(Material.ARMOR_STAND)) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("slot", slot);
            list = new ArrayList<>();
            list.add(meta);
            metadata.add(list);
        }

        return metadata;
    }

    private static void getFireworkEffect(FireworkEffect effect, List<List<Map<String, Object>>> metadata) {
        List<Map<String, Object>> color_list = new ArrayList<>();
        List<Map<String, Object>> fade_list = new ArrayList<>();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Color color : effect.getColors()) color_list.add(color.serialize());
        for (Color color : effect.getFadeColors()) fade_list.add(color.serialize());
        Map<String, Object> has_check = new HashMap<>();
        has_check.put("flicker", effect.hasFlicker());
        has_check.put("trail", effect.hasTrail());
        list.add(has_check);
        metadata.add(list);
        metadata.add(color_list);
        metadata.add(fade_list);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Player player = event.getPlayer();

        block.getState();
        BlockState state = block.getState();
        try {
            List<Object> meta = processMeta(state);
            convertByteData(meta);
        } catch (NotSerializableException e) {
            MyMaidConfig.getJDA(
                    .getTextChannelById(618569153422426113L))
                    .sendMessage("__**[CPDefectSearcher|onPlace]**__ throwed `NotSerializableException`!\n"
                    + "Message: `" + e.getMessage() + "`\n"
                    + "Location: `" + loc.toString() + "`\n"
                    + "Block: `" + block.getType().name() + "`\n"
                    + "Player: `" + player.getName() + "`")
                    .queue();
        } catch (Exception e) {
            MyMaidConfig.getJDA(
                    .getTextChannelById(618569153422426113L))
                    .sendMessage("__**[CPDefectSearcher|onPlace]**__ throwed `Exception`!\n"
                    + "Class: `" + e.getClass().getName() + "`\n"
                    + "Message: `" + e.getMessage() + "`\n"
                    + "Location: `" + loc.toString() + "`\n"
                    + "Block: `" + block.getType().name() + "`\n"
                    + "Player: `" + player.getName() + "`")
                    .queue();
        } catch (Throwable e) {
            MyMaidConfig.getJDA(
                    .getTextChannelById(618569153422426113L))
                    .sendMessage("__**[CPDefectSearcher|onPlace]**__ throwed `Throwable`!\n"
                    + "Class: `" + e.getClass().getName() + "`\n"
                    + "Message: `" + e.getMessage() + "`\n"
                    + "Location: `" + loc.toString() + "`\n"
                    + "Block: `" + block.getType().name() + "`\n"
                    + "Player: `" + player.getName() + "`")
                    .queue();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Player player = event.getPlayer();

        block.getState();
        BlockState state = block.getState();
        try {
            List<Object> meta = processMeta(state);
            convertByteData(meta);
        } catch (NotSerializableException e) {
            MyMaidConfig.getJDA(
                    .getTextChannelById(618569153422426113L))
                    .sendMessage("__**[CPDefectSearcher|onBreak]**__ throwed `NotSerializableException`!\n"
                    + "Message: `" + e.getMessage() + "`\n"
                    + "Location: `" + loc.toString() + "`\n"
                    + "Block: `" + block.getType().name() + "`\n"
                    + "Player: `" + player.getName() + "`")
                    .queue();
        } catch (Exception e) {
            MyMaidConfig.getJDA(
                    .getTextChannelById(618569153422426113L))
                    .sendMessage("__**[CPDefectSearcher|onBreak]**__ throwed `Exception`!\n"
                    + "Message: `" + e.getMessage() + "`\n"
                    + "Location: `" + loc.toString() + "`\n"
                    + "Block: `" + block.getType().name() + "`\n"
                    + "Player: `" + player.getName() + "`")
                    .queue();
        }
    }
}