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

package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CmdUsage;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Cmd_Help extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
            return true;
        }

        SendMessage(sender, cmd, "このコマンドは開発中のため使えません。");
        return true;

        /*
        Player player = (Player) sender;
        if (args.length == 1 && args[0].equalsIgnoreCase("regist")) {
            // /help regist
            // AM only
            String group = PermissionsManager.getPermissionMainGroup(player);
            if (!group.equalsIgnoreCase("Admin") && !group.equalsIgnoreCase("Moderator")) {
                // AM以外
                SendMessage(sender, cmd, "registはあなたの権限では利用できません。");
                return true;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() != Material.WRITTEN_BOOK) {
                SendMessage(sender, cmd, "このコマンドを使用するには、送る本を手に持ってください。");
                return true;
            }
            BookMeta book = (BookMeta) player.getInventory().getItemInMainHand().getItemMeta();
            if (!book.hasPages()) {
                SendMessage(sender, cmd, "エラーが発生しました。詳しくはプラグイン制作者にお問い合わせください。Debug: Pages null");
                return true;
            }

            net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            NBTTagCompound bookData = nmsItem.getTag();
            boolean res = save(bookData);
            if (res) {
                SendMessage(sender, cmd, "登録に成功しました。");
            } else {
                SendMessage(sender, cmd, "登録に失敗しました…");
            }
            return true;
        } else if (args.length != 0 && isAMR(player)) {
            player.performCommand("bukkit:help " + String.join(" ", args));
            return true;
        }
        ItemStack is = load();
        if (is == null) {
            SendMessage(sender, cmd, "現在このコマンドは動作しません。");
            return true;
        }
        if (player.getInventory().firstEmpty() == -1) {
            player.getLocation().getWorld().dropItem(player.getLocation(), is);
            SendMessage(sender, cmd, "ヘルプブックをインベントリに追加しようとしましたが、インベントリが一杯だったのであなたの足元にドロップしました。");
        } else {
            player.getInventory().addItem(is);
            SendMessage(sender, cmd, "ヘルプブックをインベントリに追加しました。");
        }
        return true;
         */
    }

    /*
    boolean save(NBTTagCompound bookData) {
        try {
            File file = new File(Main.getJavaPlugin().getDataFolder(), "helpBook.yml");

            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            if (data.contains("nbt")) {
                data.set("nbt_bak" + System.currentTimeMillis() / 1000, data.getString("nbt"));
            }
            data.set("nbt", bookData.toString());
            data.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    ItemStack load() {
        File file = new File(Main.getJavaPlugin().getDataFolder(), "helpBook.yml");
        if (!file.exists()) {
            return null;
        }
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        if (!data.contains("nbt")) {
            return null;
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = new net.minecraft.server.v1_12_R1.ItemStack(
                Item.getById(387));
        try {
            NBTBase nbtbase = MojangsonParser.parse(data.getString("nbt"));
            nmsItem.setTag((NBTTagCompound) nbtbase);
            ItemStack book = CraftItemStack.asBukkitCopy(nmsItem);
            return book;
        } catch (MojangsonParseException e) {
            e.printStackTrace();
            return null;
        }
    }
     */

    @Override
    public String getDescription() {
        return "ヘルプブックを入手します。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "help",
                new CmdUsage.Cmd("", "ヘルプブックを入手します。"),
                new CmdUsage.Cmd("regist", "ヘルプブックを登録します。運営のみ使用できます。")
        );
    }
}
