package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.TpDeny;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.logging.Logger;

public class Event_CommandTP extends MyMaidLibrary implements Listener {
    @EventHandler
    public void onTeleportCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        String[] args = command.split(" ", 0);
        if (args.length == 0) {
            return; // 本来発生しないと思うけど
        }
        if (!args[0].equalsIgnoreCase("/tp")
                && !args[0].equalsIgnoreCase("/minecraft:tp")) {
            return; // tpコマンド以外
        }
        // /tp [target player] <destination player>
        // /tp [target player] <x> <y> <z> [<yaw> <pitch>]
        if (args.length == 2) { // /tp <Player>
            String to = args[1];
            Player to_player = Bukkit.getPlayerExact(to);
            if (to_player == null) {
                return;
            }
            printDebugMsg("<" + player.getName() + "> " + player.getName() + " ==> " + to_player.getName());
            if (to_player.getGameMode() == GameMode.SPECTATOR) { // テレポート先がスペクテイター
                printDebugMsg("NG スペクテイタープレイヤー");
                printDebugMsg(player.getGameMode().name() + " => " + to_player.getGameMode().name());
                if (!isAMR(player)) { // 実行者LQD
                    printDebugMsg("スペクテイタープレイヤーにテレポートしようとしたため規制しました。");
                    event.setCancelled(true);
                } else {
                    if (player.getGameMode() != GameMode.SPECTATOR) {
                        printDebugMsg("スペクテイタープレイヤーにテレポートしようとしたため規制しました。");
                        SendMessage(player, "tp", "プレイヤー「" + to_player.getName() + "」はスペクテイターモードのためテレポートできません。");
                        event.setCancelled(true);
                    }
                }
            } else {
                // TpDeny
                TpDeny tpDeny = new TpDeny(to_player);
                if (tpDeny.isTpDeny(player)) {
                    // denied
                    printDebugMsg("tpDenyによって拒否されました。");
                    SendMessage(player, "tpDeny", "指定されたプレイヤー「" + to_player.getName() + "」へのテレポートは拒否されました。");
                    SendMessage(to_player, "tpDeny", "プレイヤー「" + player.getName() + "」からのテレポートを拒否しました。");
                    event.setCancelled(true);
                }
            }
        } else if (args.length == 3) { // /tp <Player> <Player>
            String from = args[1];
            String to = args[2];
            Player from_player = Bukkit.getPlayerExact(from);
            Player to_player = Bukkit.getPlayerExact(to);
            if (from_player == null || to_player == null) {
                return;
            }
            printDebugMsg("<" + player.getName() + "> " + from_player.getName() + " ==> " + to_player.getName());
            if (from_player.getUniqueId().equals(player.getUniqueId())) {
                printDebugMsg("OK 自分をテレポート");
            } else if (!from_player.getUniqueId().equals(player.getUniqueId())) {
                printDebugMsg("NG 他人をテレポート");
                if (!isAMR(player)) { // 実行者LQD
                    printDebugMsg("他人をテレポートさせようとしたため規制しました。");
                    event.setCancelled(true);
                }
            }
            if (to_player.getGameMode() == GameMode.SPECTATOR) {
                printDebugMsg("NG スペクテイタープレイヤー");
                printDebugMsg(from_player.getGameMode().name() + " => " + to_player.getGameMode().name());
                if (!isAMR(player)) { // 実行者LQD
                    printDebugMsg("スペクテイタープレイヤーにテレポートしようとしたため規制しました。");
                    event.setCancelled(true);
                } else {
                    if (from_player.getGameMode() != GameMode.SPECTATOR) {
                        printDebugMsg("スペクテイタープレイヤーにテレポートしようとしたため規制しました。");
                        SendMessage(player, "tp", "プレイヤー「" + to_player.getName() + "」はスペクテイターモードのためテレポートできません。");
                        event.setCancelled(true);
                    }
                }
            } else {
                // TpDeny
                TpDeny tpDeny = new TpDeny(to_player);
                if (tpDeny.isTpDeny(player) || tpDeny.isTpDeny(from_player)) {
                    // denied
                    printDebugMsg("tpDenyによって拒否されました。");
                    SendMessage(player, "tpDeny", "指定されたプレイヤー「" + to_player.getName() + "」へのテレポートは拒否されました。");
                    SendMessage(to_player, "tpDeny", "プレイヤー「" + player.getName() + "」からのテレポートを拒否しました。");
                    event.setCancelled(true);
                }
            }
        } else if (args.length == 4) { // /tp ~ ~ ~
            String x = args[1];
            String y = args[2];
            String z = args[3];

            printDebugMsg("<" + player.getName() + "> " + player.getName() + " ==> " + x + " " + y + " " + z);
        } else if (args.length == 5) { // /tp <Player> ~ ~ ~
            String from = args[1];
            String x = args[2];
            String y = args[3];
            String z = args[4];
            Player from_player = Bukkit.getPlayer(from);
            if (from_player == null) {
                return;
            }
            printDebugMsg("<" + player.getName() + "> " + from_player.getName() + " ==> " + x + " " + y + " " + z);
            if (!from_player.getUniqueId().equals(player.getUniqueId())) {
                printDebugMsg("NG 他人をテレポート");
                if (!isAMR(player)) { // 実行者LQD
                    printDebugMsg("他人をテレポートさせようとしたため規制しました。");
                    event.setCancelled(true);
                }
            }
        } else if (args.length == 6) { // /tp ~ ~ ~ 0 0
            String x = args[1];
            String y = args[2];
            String z = args[3];
            String yaw = args[4];
            String pitch = args[5];

            printDebugMsg("<" + player.getName() + "> " + player.getName() + " ==> " + x + " " + y + " " + z + " "
                    + yaw + " " + pitch);
        } else if (args.length == 7) { // /tp <Player> ~ ~ ~ 0 0
            String from = args[1];
            String x = args[2];
            String y = args[3];
            String z = args[4];
            String yaw = args[5];
            String pitch = args[6];
            Player from_player = Bukkit.getPlayer(from);
            if (from_player == null) {
                return;
            }

            printDebugMsg("<" + player.getName() + "> " + from_player.getName() + " ==> " + x + " " + y + " " + z
                    + " " + yaw + " " + pitch);
            if (!from_player.getUniqueId().equals(player.getUniqueId())) {
                printDebugMsg("NG 他人をテレポート");
                if (!isAMR(player)) { // 実行者LQD
                    printDebugMsg("他人をテレポートさせようとしたため規制しました。");
                    event.setCancelled(true);
                }
            }
        }
    }

    void printDebugMsg(String msg) {
        boolean debug = true;
        if (!debug)
            return;
        Logger LOGGER = Bukkit.getLogger();
        LOGGER.info(msg);
    }
}
