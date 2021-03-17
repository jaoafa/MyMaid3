package com.jaoafa.MyMaid3.Event;

import com.jaoafa.MyMaid3.Lib.EBan;
import com.jaoafa.MyMaid3.Lib.Jail;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.SelectorParser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Event_AntiProblemCmd extends MyMaidLibrary implements Listener {
    static final Map<String, AntiCommand> antiCommandMap = new HashMap<>();
    static final String[] LeastOne = new String[]{"r", "type", "team", "name"};

    static {
        antiCommandMap.put("/kill", new AntiCmd_Kill());
        antiCommandMap.put("/minecraft:kill", new AntiCmd_Kill());
        antiCommandMap.put("/pex", new AntiCmd_PexPromote());
        antiCommandMap.put("//calc", new AntiCmd_WECalc());
        antiCommandMap.put("/worldedit:/calc", new AntiCmd_WECalc());
        antiCommandMap.put("//eval", new AntiCmd_WEEval());
        antiCommandMap.put("/worldedit:/eval", new AntiCmd_WEEval());
        antiCommandMap.put("/god", new AntiCmd_WGGod());
        antiCommandMap.put("/worldguard:god", new AntiCmd_WGGod());
        antiCommandMap.put("/pl", new AntiCmd_PluginCmd());
        antiCommandMap.put("/bukkit:pl", new AntiCmd_PluginCmd());
        antiCommandMap.put("/plugins", new AntiCmd_PluginCmd());
        antiCommandMap.put("/bukkit:plugins", new AntiCmd_PluginCmd());
        antiCommandMap.put("/rl", new AntiCmd_ReloadCmd());
        antiCommandMap.put("/bukkit:rl", new AntiCmd_ReloadCmd());
        antiCommandMap.put("/reload", new AntiCmd_ReloadCmd());
        antiCommandMap.put("/bukkit:reload", new AntiCmd_ReloadCmd());
        antiCommandMap.put("/ban", new AntiCmd_BanCmd());
        antiCommandMap.put("/bukkit:ban", new AntiCmd_BanCmd());
        antiCommandMap.put("/mcbans:ban", new AntiCmd_BanCmd());
        antiCommandMap.put("/kick", new AntiCmd_KickCmd());
        antiCommandMap.put("/bukkit:kick", new AntiCmd_KickCmd());
        antiCommandMap.put("/mcbans:kick", new AntiCmd_KickCmd());
        antiCommandMap.put("/ver", new AntiCmd_VersionCmd());
        antiCommandMap.put("/bukkit:ver", new AntiCmd_VersionCmd());
        antiCommandMap.put("/version", new AntiCmd_VersionCmd());
        antiCommandMap.put("/bukkit:version", new AntiCmd_VersionCmd());
        antiCommandMap.put("/stop", new AntiCmd_StopCmd());
        antiCommandMap.put("/bukkit:stop", new AntiCmd_StopCmd());
        antiCommandMap.put("/minecraft:stop", new AntiCmd_StopCmd());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        String[] args = command.split(" ");
        if (args.length == 0) {
            return; // 本来発生しないと思うけど
        }

        Optional<Map.Entry<String, AntiCommand>> func = antiCommandMap.entrySet().stream().filter(cmd -> cmd.getKey().equalsIgnoreCase(args[0])).findFirst();
        if (!func.isPresent()) {
            return;
        }

        EBan eban = new EBan(player);
        if (eban.isBanned()) {
            event.setCancelled(true);
            return;
        }
        Jail jail = new Jail(player);
        if (jail.isBanned()) {
            event.setCancelled(true);
            return;
        }

        func.get().getValue().execute(event, player, args);
    }

    interface AntiCommand {
        void execute(PlayerCommandPreprocessEvent event, Player player, String[] args);
    }

    static class AntiCmd_Kill implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            if (args.length == 1) {
                return;
            }
            if (args[1].equalsIgnoreCase("@p") || args[1].equalsIgnoreCase(player.getName())) {
                return;
            }

            if (args[1].equalsIgnoreCase("@e")) {
                player.chat("キリトかなーやっぱりww");
                player.chat("自分は思わないんだけど周りにキリトに似てるってよく言われるwww");
                player.chat("こないだDQNに絡まれた時も気が付いたら意識無くて周りに人が血だらけで倒れてたしなwww");
                player.chat("ちなみに彼女もアスナに似てる(聞いてないw)");
                player.chat("(私は\"" + String.join(" ", args) + "\"コマンドを使用しました。)");
                event.setCancelled(true);
                MyMaidLibrary.checkSpam(player);
                return;
            }
            if (args[1].equalsIgnoreCase("@a")) {
                player.chat("キリトかなーやっぱw");
                player.chat("一応オタクだけど彼女いるし、俺って退けない性格だしそこら辺とかめっちゃ似てるって言われる()");
                player.chat("握力も31キロあってクラスの女子にたかられる←彼女いるからやめろ！笑");
                player.chat("俺、これでも中1ですよ？");
                player.chat("(私は\"" + String.join(" ", args) + "\"コマンドを使用しました。)");
                event.setCancelled(true);
                MyMaidLibrary.checkSpam(player);
                return;
            }
            if (args[1].startsWith("@e") && !MyMaidLibrary.isAMR(player)) {
                // DV
                player.chat("最後にキレたのは高2のころかな。オタクだからってウェイ系に絡まれたときw");
                player.chat(
                        "最初は微笑してたんだけど、推しを貶されて気づいたらウェイ系は意識無くなってて、25人くらいに取り押さえられてたw記憶無いけど、ひたすら笑顔で殴ってたらしいw俺ってサイコパスなのかもなww");
                player.chat("(私は\"" + String.join(" ", args) + "\"コマンドを使用しました。)");
                event.setCancelled(true);
                MyMaidLibrary.checkSpam(player);
                return;
            }
            if (!MyMaidLibrary.isAMR(player)) {
                if (player.getName().equalsIgnoreCase(args[1])) {
                    event.setCancelled(true);
                    return;
                }
                String text = args[0].equalsIgnoreCase("/kill")
                        ? String.format("%sさんが%sを殺すとか調子に乗ってると思うので%sさんを殺しておきますね^^", player.getName(), args[1], player.getName())
                        : String.format("%sごときが%sを殺そうだなんて図が高いわ！ %sが死にな！", player.getName(), args[1], player.getName());
                MyMaidLibrary.chatFake(ChatColor.GOLD, "jaotan", text);
                player.setHealth(0);
                event.setCancelled(true);
                MyMaidLibrary.checkSpam(player);
                return;
            }
            if (args[1].startsWith("@e")) {
                try {
                    SelectorParser parser = new SelectorParser(args[1]);
                    if (!parser.isValidValues()) {
                        player.sendMessage(String.format("[COMMAND] %s指定されたセレクターは適切でありません。", ChatColor.GREEN));
                        Set<String> invalids = parser.getInValidValues();
                        player.sendMessage(String.format("[COMMAND] %s不適切だったセレクター引数: %s", ChatColor.GREEN, String.join(", ", invalids)));
                        event.setCancelled(true);
                        MyMaidLibrary.checkSpam(player);
                        return;
                    }
                    if (!parser.getArgs().containsKey("r")) {
                        boolean exist = false;
                        for (String one : LeastOne) {
                            if (parser.getArgs().containsKey(one)) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            player.sendMessage(String.format("[COMMAND] %s指定されたセレクターは適切でありません。", ChatColor.GREEN));
                            player.sendMessage(String.format("[COMMAND] %s理由: @eセレクターで引数「%s」のいずれかを指定せずに実行することはできません。", ChatColor.GREEN, String.join("」・「", LeastOne)));
                            event.setCancelled(true);
                            MyMaidLibrary.checkSpam(player);
                            return;
                        }
                    }
                    if (parser.getArgs().containsKey("r")) {
                        if (Integer.parseInt(parser.getArgs().get("r")) >= 300) {
                            player.sendMessage(String.format("[COMMAND] %s指定されたセレクターは適切でありません。", ChatColor.GREEN));
                            player.sendMessage(
                                    String.format("[COMMAND] %s理由: @eセレクターで引数「r」に300以上の値を指定することはできません。", ChatColor.GREEN));
                            event.setCancelled(true);
                            MyMaidLibrary.checkSpam(player);
                            return;
                        }
                    } else {
                        player.sendMessage(String.format("[COMMAND] %s指定されたセレクターは適切でありません。", ChatColor.GREEN));
                        player.sendMessage(String.format("[COMMAND] %s理由: @eセレクターで引数「r」を指定せずに実行することはできません。", ChatColor.GREEN));
                        event.setCancelled(true);
                        MyMaidLibrary.checkSpam(player);
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    player.sendMessage(String.format("[COMMAND] %s指定されたセレクターは適切でありません。", ChatColor.GREEN));
                    player.sendMessage(String.format("[COMMAND] %s理由: %s", ChatColor.GREEN, e.getMessage()));
                    event.setCancelled(true);
                    MyMaidLibrary.checkSpam(player);
                    return;
                }
            }
            if (args[1].startsWith("@a")) {
                try {
                    SelectorParser parser = new SelectorParser(args[1]);
                    if (!parser.isValidValues()) {
                        player.sendMessage(String.format("[COMMAND] %s指定されたセレクターは適切でありません。", ChatColor.GREEN));
                        event.setCancelled(true);
                        MyMaidLibrary.checkSpam(player);
                    }
                } catch (IllegalArgumentException e) {
                    player.sendMessage(String.format("[COMMAND] %s指定されたセレクターは適切でありません。", ChatColor.GREEN));
                    player.sendMessage(String.format("[COMMAND] %s理由: %s", ChatColor.GREEN, e.getMessage()));
                    event.setCancelled(true);
                    MyMaidLibrary.checkSpam(player);
                }
            }
        }
    }

    static class AntiCmd_PexPromote implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            player.chat("(◞‸◟) ｻﾊﾞｵﾁﾅｲｰﾅ? ﾎﾜｯｳｳﾞｼﾞｸｼﾞｸﾞｨﾝﾉﾝﾞﾝﾞﾝﾞﾝﾞﾍﾟﾗﾚｸﾞｼﾞｭﾁﾞ…ﾇﾇﾉｮｩﾂﾋﾞｮﾝﾇｽﾞｨｺｹｰｯﾝｦｯ…ｶﾅｼﾐ…");
            player.chat("(私は\"" + String.join(" ", args) + "\"コマンドを使用しました。)");
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_WECalc implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            player.chat("オ、オオwwwwwwwwオレアタマ良いwwwwwwww最近めっちょ成績あがってんねんオレwwwwwwwwエゴサとかかけるとめっちょ人気やねんwwwwァァァァァァァwwwクソハゲアタマを見下しながら食べるフライドチキンは一段とウメェなァァァァwwwwwwww");
            player.chat("(私は\"" + String.join(" ", args) + "\"コマンドを使用しました。)");
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_WEEval implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            player.chat("オ、オオwwwwwwwwオレコマンド実行できるwwwwwwww最近マイクラやってんねんオレwwwwwwwwカスどもをぶちのめしてるねんwwwwァァァァァァァwwwカスに見下されながら食べるフィレオフィッシュは一段とウメェなァァァァwwwwwwww");
            player.chat("(私は\"" + String.join(" ", args) + "\"コマンドを使用しました。)");
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_WGGod implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            player.chat("オ、オオwwwwwwwwオレアルファwwwwwwww最近めっちょふぁぼられてんねんオレwwwwwwwwエゴサとかかけるとめっちょ人気やねんwwwwァァァァァァァwwwクソアルファを見下しながら食べるエビフィレオは一段とウメェなァァァァwwwwwwww");
            player.chat("(私は\"" + String.join(" ", args) + "\"コマンドを使用しました。)");
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_PluginCmd implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            if (isAMR(player)) {
                return;
            }
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_ReloadCmd implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            if (isAM(player)) {
                return;
            }
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_BanCmd implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            if (isAM(player)) {
                return;
            }
            player.chat("†エンゲキ†...");
            player.chat(
                    "私達の世界は…演劇で溢れています…その劇を演じる者…受け入れて消費する者…全ての者がそれに魅了されます…舞台の上に上がり…世界に自分の価値をはからせましょう…その舞台が…現実のものであるかないかにかかわらず…私達は…私達の役を演じるのです…しかし…それらの役割を無くしてしまったら…私達は一体何者なのでしょう…人々が、善と悪を区別しなくなり…目に見える世界が失われ…舞台の幕が降ろされてしまったら…私達は…本当の自分達であること…それが…生きているということなのでしょうか…魂を…持っているということなのでしょうか……＼キイイイイイイイン！！！！！！！！！／");
            player.chat("(私は\"" + String.join(" ", args) + "\"コマンドを使用しました。)");
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_KickCmd implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            if (isAM(player)) {
                return;
            }
            player.setHealth(0.0D);
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_VersionCmd implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            if (isAMR(player)) {
                return;
            }
            checkSpam(player);
            event.setCancelled(true);
        }
    }

    static class AntiCmd_StopCmd implements AntiCommand {
        @Override
        public void execute(PlayerCommandPreprocessEvent event, Player player, String[] args) {
            if (isAM(player)) {
                return;
            }
            player.setHealth(0.0D);
            checkSpam(player);
            event.setCancelled(true);
        }
    }
}
