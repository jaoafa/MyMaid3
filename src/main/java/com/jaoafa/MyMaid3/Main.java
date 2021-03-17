package com.jaoafa.MyMaid3;

import com.jaoafa.MyMaid3.DiscordEvent.Event_Ready;
import com.jaoafa.MyMaid3.DiscordEvent.Event_ServerChatListCmd;
import com.jaoafa.MyMaid3.DiscordEvent.Event_ServerLeave;
import com.jaoafa.MyMaid3.HttpServer.MyMaidServer;
import com.jaoafa.MyMaid3.Lib.*;
import com.jaoafa.MyMaid3.Task.*;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class Main extends JavaPlugin {
    private static Main Main = null;

    public static void DiscordExceptionError(Class<?> clazz, MessageChannel channel, Throwable exception) {
        if (channel == null && MyMaidConfig.getReportChannel() != null) {
            channel = MyMaidConfig.getReportChannel();
        } else if (channel == null) {
            System.out.println("DiscordExceptionError: channel == null and Main.ReportChannel == null.");
            System.out.println("DiscordExceptionError did not work properly!");
            return;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.flush();
        InputStream is = new ByteArrayInputStream(sw.toString().getBytes(StandardCharsets.UTF_8));
        channel.sendMessage(":pencil:おっと！MyMaid3のDiscord関連でなにか問題が発生したようです！ <@221991565567066112>\\n**ErrorMsg**: `"
                + exception.getMessage()
                + "`\n**Class**: `" + clazz.getName() + " (" + exception.getClass().getName() + ")`").queue();
        channel.sendFile(is, "stacktrace.txt").queue();
    }

    public static JavaPlugin getJavaPlugin() {
        return Main;
    }

    public static Main getMain() {
        return Main;
    }

    public static void setMain(Main main) {
        Main = main;
    }

    /**
     * プラグインが起動したときに呼び出し
     *
     * @author mine_book000
     * @since 2019/08/20
     */
    @Override
    public void onEnable() {
        setMain(this);

        loadConfig();
        if (!isEnabled())
            return;

        commandRegister();
        if (!isEnabled())
            return;

        registryEvent();
        if (!isEnabled())
            return;

        scheduleTask();
        if (!isEnabled())
            return;

        TPSChecker.OnEnable_TPSSetting();

        for (World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setStorm(false);
        }
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        if (!config.contains("discordtoken")) {
            getLogger().warning("Discordへの接続に失敗しました。(コンフィグにトークンが設定されていません)");
            getLogger().warning("MyMaid3プラグインを終了します。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            JDABuilder jdabuilder = JDABuilder.createDefault(config.getString("discordtoken"))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .setContextEnabled(false)
                    .setEventManager(new AnnotatedEventManager());

            registryDiscordEvent(jdabuilder);

            MyMaidConfig.setJDA(jdabuilder.build().awaitReady());
        } catch (Exception e) {
            getLogger().warning("Discordへの接続に失敗しました。(" + e.getMessage() + ")");
            getLogger().warning("MyMaid3プラグインを終了します。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (config.contains("bugreportwebhookurl")) {
            MyMaidConfig.setBugReportWebhookUrl(config.getString("bugreportwebhookurl"));
        }
        if (config.contains("feedbackwebhookurl")) {
            MyMaidConfig.setFeedbackWebhookUrl(config.getString("feedbackwebhookurl"));
        }

        if (!config.contains("sqlserver") || !config.contains("sqlport") || !config.contains("sqldatabase")
                || !config.contains("sqluser") || !config.contains("sqlpassword")) {
            getLogger().warning("MySQLへの接続に失敗しました。(コンフィグにSQL接続情報が設定されていません)");
            getLogger().warning("MyMaid3プラグインを終了します。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            MyMaidConfig.setMySQLDBManager(new MySQLDBManager(
                    config.getString("sqlserver"),
                    config.getString("sqlport"),
                    config.getString("sqldatabase"),
                    config.getString("sqluser"),
                    config.getString("sqlpassword")));
        } catch (ClassNotFoundException e) {
            getLogger().warning("MySQLへの接続に失敗しました。(MySQL接続するためのクラスが見つかりません)");
            getLogger().warning("MyMaid3プラグインを終了します。");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (config.contains("development") && config.getBoolean("development")) {
            getLogger().warning("THIS SERVER IS DEVELOPMENT SERVER. Some features will be disabled.");
            MyMaidConfig.setDevelopmentServer(true);
        }
    }

    private void commandRegister() {
        try {
            ClassFinder classFinder = new ClassFinder(this.getClassLoader());
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.MyMaid3.Command")) {
                if (!clazz.getName().startsWith("com.jaoafa.MyMaid3.Command.Cmd_")) {
                    continue;
                }
                if (clazz.getEnclosingClass() != null) {
                    continue;
                }
                if (clazz.getName().contains("$")) {
                    continue;
                }
                String commandName = clazz.getName().substring("com.jaoafa.MyMaid3.Command.Cmd_".length())
                        .toLowerCase();
                Constructor<?> construct = clazz.getConstructor();
                Object instance = construct.newInstance();
                CommandPremise cmdPremise = (CommandPremise) instance;

                PluginCommand cmd = getCommand(commandName);
                if(cmd == null){
                    getLogger().info(commandName + ": command register failed [0]");
                    return;
                }
                cmd.setName(commandName);
                cmd.setDescription(cmdPremise.getDescription());
                cmd.setPermission("mymaid." + commandName);
                List<String> usages = new LinkedList<>();
                for (CmdUsage.Cmd _cmd : cmdPremise.getUsage().getCommands()){
                    usages.add("・" + cmdPremise.getUsage().getCommand() + " " + _cmd.getArgs() + ": " + _cmd.getDetails());
                }
                cmd.setUsage(String.join("\n", usages));

                if (instance instanceof CommandExecutor) {
                    try {
                        CommandExecutor cmdExecutor = (CommandExecutor) instance;
                        cmd.setExecutor(cmdExecutor);
                    } catch (ClassCastException e) {
                        // commandexecutor not implemented
                        getLogger().info(commandName + ": commandexecutor not implemented [1]");
                        continue;
                    }
                } else {
                    getLogger().info(commandName + ": commandexecutor not implemented [2]");
                    continue;
                }

                if (instance instanceof TabCompleter) {
                    try {
                        TabCompleter cmdCompleter = (TabCompleter) instance;
                        cmd.setTabCompleter(cmdCompleter);
                    } catch (ClassCastException e) {
                        // tabcompleter not implemented
                        getLogger().info(commandName + ": tabcompleter not implemented");
                    }
                }
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                bukkitCommandMap.setAccessible(true);
                CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                boolean bool = cmd.register(commandMap);
                getLogger().info(commandName + " register: " + bool);
            }
        } catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
            e.printStackTrace();
        }
    }

    private void registryEvent() {
        try {
            ClassFinder classFinder = new ClassFinder(this.getClassLoader());
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.MyMaid3.Event")) {
                if (!clazz.getName().startsWith("com.jaoafa.MyMaid3.Event.Event_")) {
                    continue;
                }
                if (clazz.getEnclosingClass() != null) {
                    continue;
                }
                if (clazz.getName().contains("$")) {
                    continue;
                }

                Constructor<?> construct = clazz.getConstructor();
                Object instance = construct.newInstance();

                if (instance instanceof Listener) {
                    try {
                        Listener listener = (Listener) instance;
                        getServer().getPluginManager().registerEvents(listener, this);
                        getLogger().info(clazz.getSimpleName() + " registered");
                    } catch (ClassCastException e) {
                        // commandexecutor not implemented
                        getLogger().info(clazz.getSimpleName() + ": Listener not implemented [1]");
                    }
                } else {
                    getLogger().info(clazz.getSimpleName() + ": Listener not implemented [2]");
                }
            }
        } catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
            e.printStackTrace();
        }
    }

    private void registryDiscordEvent(JDABuilder d) {
        d.addEventListeners(new Event_Ready());
        d.addEventListeners(new Event_ServerLeave());
        d.addEventListeners(new Event_ServerChatListCmd());
    }

    private void scheduleTask() {
        //new Task_AFK().runTaskTimerAsynchronously(this, 0L, 1200L);
        new Task_AutoRemoveTeam().runTaskTimer(this, 0L, 1200L); // per 1 minute
        new Task_TPSTimings(this).runTaskLater(this, 1200L); // per 1 minute
        new Task_NewStep().runTaskTimerAsynchronously(this, 0L, 1200L); // per 1 minute
        new Task_DisableInvisible().runTaskTimer(this, 0L, 1200L); // per 1 minute
        new Task_OldWorldCheck().runTaskTimer(this, 0L, 12000L); // per 10 minutes

        new MyMaidServer().runTaskAsynchronously(this);
    }
}
