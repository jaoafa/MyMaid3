package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class Cmd_TpDeny extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, getDescription(), getUsage());
            return true;
        }
        if (!(sender instanceof Player)) {
            SendMessage(sender, cmd, "このコマンドはサーバ内から実行できます。");
            return true;
        }
        Player player = (Player) sender;
        if (MyMaidConfig.getMySQLDBManager() == null) {
            SendMessage(sender, cmd, "データベースサーバに接続できません。時間をあけてからもう一度お試しください。");
            return true;
        }
        TpDeny tpDeny = new TpDeny(player);
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("notify")) {
                OfflinePlayer target = getOfflinePlayer(args[1]);
                boolean notifySetting;
                if (args[2].equalsIgnoreCase("On")) {
                    notifySetting = true;
                } else if (args[2].equalsIgnoreCase("Off")) {
                    notifySetting = false;
                } else {
                    SendMessage(sender, cmd, "第3引数にはOnまたはOffを指定してください。");
                    return true;
                }
                boolean bool = tpDeny.setNotify(target, notifySetting);
                SendMessage(sender, cmd, "指定されたプレイヤー「" + target.getName() + "」の通知設定を" + (notifySetting ? "オン" : "オフ") + "にすることに" + (bool ? "成功" : "失敗") + "しました。");
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                OfflinePlayer target = getOfflinePlayer(args[1]);
                if (tpDeny.isTpDeny(target)) {
                    SendMessage(sender, cmd, "指定されたプレイヤーは既にテレポートを拒否しています。");
                    return true;
                }
                boolean bool = tpDeny.addDeny(target);
                SendMessage(sender, cmd, "指定されたプレイヤー「" + target.getName() + "」のテレポート拒否に" + (bool ? "成功" : "失敗") + "しました。");
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                OfflinePlayer target = getOfflinePlayer(args[1]);
                boolean bool = tpDeny.disableDeny(target);
                SendMessage(sender, cmd, "指定されたプレイヤー「" + target.getName() + "」のテレポート拒否解除に" + (bool ? "成功" : "失敗") + "しました。");
                return true;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<TpDeny.TpDenyData> denys = tpDeny.getDenys();
                SendMessage(sender, cmd, "TpDeny list / count: " + denys.size());
                for (TpDeny.TpDenyData denydata : denys) {
                    SendMessage(sender, cmd, "[" + denydata.id + "] " + denydata.target.getName() + " / created_at: " + MyMaidLibrary.sdfFormat(denydata.created_at) + " / updated_at: " + MyMaidLibrary.sdfFormat(denydata.updated_at));
                }
                SendMessage(sender, cmd, "テレポート拒否を解除したい場合は、かっこ内に記載されている数値(TpDenyId)を元に/tpdeny remove <TpDenyId>を実行してください。");
                return true;
            }
        }
        SendUsageMessage(sender, getDescription(), getUsage());
        return true;
    }

    OfflinePlayer getOfflinePlayer(String name_or_uuid) {
        return MyMaidLibrary.isUUID(name_or_uuid) ?
                Bukkit.getOfflinePlayer(UUID.fromString(name_or_uid)) :
                Bukkit.getOfflinePlayer(name_or_uuid);
    }

    @Override
    public String getDescription() {
        return "TpDeny(特定ユーザーからのテレポート拒否)の設定をします。";
    }

    @Override
    public CmdUsage getUsage() {
        return new CmdUsage(
                "tpdeny",
                new CmdUsage.Cmd("add <PlayerName>", "TpDenyにプレイヤーを追加し、以降のテレポートを拒否します。"),
                new CmdUsage.Cmd("remove <PlayerName>", "TpDenyからプレイヤーを解除し、以降のテレポートを許可します。"),
                new CmdUsage.Cmd("notify <PlayerName> <On/Off>", "テレポートを拒否した場合に通知するかどうかを設定します。"),
                new CmdUsage.Cmd("list", "現在TpDenyに追加されている(テレポートを拒否されている)プレイヤーの一覧を表示します。")
        );
    }
}
