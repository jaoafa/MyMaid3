package com.jaoafa.MyMaid3.Command;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.TpDeny;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cmd_TpDeny extends MyMaidLibrary implements CommandExecutor, CommandPremise {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            SendUsageMessage(sender, cmd);
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
        if (args.length == 2) {
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
                if (!isInt(args[1]) || Integer.parseInt(args[1]) <= 0) {
                    SendMessage(sender, cmd, "引数の指定に問題があります。1以上の数値(TpDenyId)を指定してください。");
                    return true;
                }
                boolean bool = tpDeny.disableDeny(Integer.parseInt(args[1]));
                SendMessage(sender, cmd, "指定されたTpDenyId「" + args[1] + "」のテレポート拒否解除に" + (bool ? "成功" : "失敗") + "しました。");
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
        SendUsageMessage(sender, cmd);
        return true;
    }

    OfflinePlayer getOfflinePlayer(String name_or_uuid) {
        return MyMaidLibrary.isUUID(name_or_uuid) ?
                Bukkit.getOfflinePlayer(UUID.fromString(name_or_uuid)) :
                Bukkit.getOfflinePlayer(name_or_uuid);
    }

    @Override
    public String getDescription() {
        return "TpDeny(特定ユーザーからのテレポート拒否)の設定をします。";
    }

    @Override
    public List<String> getUsage() {
        return new ArrayList<String>() {
            {
                add("/tpdeny add <Player>: TpDenyにプレイヤーを追加し、以降のテレポートを拒否します。");
                add("/tpdeny remove <TpDenyId>: TpDenyからプレイヤーを解除し、以降のテレポートを許可します。TpDenyIdは/tpdeny listで取得してください。");
                add("/tpdeny list: 現在TpDenyに追加されている(テレポートを拒否されている)プレイヤーの一覧を表示します。");
            }
        };
    }
}
