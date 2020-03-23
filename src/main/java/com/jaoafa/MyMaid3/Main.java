package com.jaoafa.MyMaid3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.MyMaid3.DiscordEvent.Event_Ready;
import com.jaoafa.MyMaid3.DiscordEvent.Event_ServerLeave;
import com.jaoafa.MyMaid3.Lib.ClassFinder;
import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MySQLDBManager;
import com.jaoafa.MyMaid3.Lib.PermissionsManager;
import com.jaoafa.MyMaid3.Task.Task_AFK;
import com.jaoafa.MyMaid3.Task.Task_AutoRemoveTeam;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;

public class Main extends JavaPlugin {
	private static Main Main = null;
	private static JDA JDA = null;
	public static MySQLDBManager MySQLDBManager = null;
	public static TextChannel ReportChannel = null;
	public static TextChannel ServerChatChannel = null;
	public static String MCBansRepAPI = null;
	public static String BugReportWebhookUrl = null;

	/**
	 * プラグインが起動したときに呼び出し
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

		registEvent();
		if (!isEnabled())
			return;

		PermissionsManager.first();
		if (!isEnabled())
			return;
		scheduleTask();
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
			JDABuilder jdabuilder = new JDABuilder(AccountType.BOT)
					.setAutoReconnect(true)
					.setBulkDeleteSplittingEnabled(false)
					.setToken(config.getString("discordtoken"))
					.setContextEnabled(false)
					.setEventManager(new AnnotatedEventManager());

			jdabuilder = registDiscordEvent(jdabuilder);

			JDA = jdabuilder.build().awaitReady();
		} catch (Exception e) {
			getLogger().warning("Discordへの接続に失敗しました。(" + e.getMessage() + ")");
			getLogger().warning("MyMaid3プラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (config.contains("bugreportwebhookurl")) {
			BugReportWebhookUrl = config.getString("bugreportwebhookurl");
		}

		if (!config.contains("sqlserver") || !config.contains("sqlport") || !config.contains("sqldatabase")
				|| !config.contains("sqluser") || !config.contains("sqlpassword")) {
			getLogger().warning("MySQLへの接続に失敗しました。(コンフィグにSQL接続情報が設定されていません)");
			getLogger().warning("MyMaid3プラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (config.contains("MCBansRepAPI")) {
			MCBansRepAPI = config.getString("MCBansRepAPI");
		} else {
			getLogger().warning("コンフィグにMCBansRepAPIが記載されていなかったため、Reputationチェック処理は動作しません。");
		}

		try {
			MySQLDBManager = new MySQLDBManager(
					config.getString("sqlserver"),
					config.getString("sqlport"),
					config.getString("sqldatabase"),
					config.getString("sqluser"),
					config.getString("sqlpassword"));
		} catch (ClassNotFoundException e) {
			getLogger().warning("MySQLへの接続に失敗しました。(MySQL接続するためのクラスが見つかりません)");
			getLogger().warning("MyMaid3プラグインを終了します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
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
				cmd.setName(commandName);
				cmd.setDescription(cmdPremise.getDescription());
				cmd.setPermission("mymaid." + commandName);
				cmd.setUsage(String.join("\n", cmdPremise.getUsage()));

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
				getLogger().info(commandName + " register: " + Boolean.valueOf(bool));
			}
		} catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
			e.printStackTrace();
			return;
		}
	}

	private void registEvent() {
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

				Constructor<?> construct = (Constructor<?>) clazz.getConstructor();
				Object instance = construct.newInstance();

				if (instance instanceof Listener) {
					try {
						Listener listener = (Listener) instance;
						getServer().getPluginManager().registerEvents(listener, this);
						getLogger().info(clazz.getSimpleName() + " registered");
					} catch (ClassCastException e) {
						// commandexecutor not implemented
						getLogger().info(clazz.getSimpleName() + ": Listener not implemented [1]");
						continue;
					}
				} else {
					getLogger().info(clazz.getSimpleName() + ": Listener not implemented [2]");
					continue;
				}
			}
		} catch (Exception e) { // ClassFinder.findClassesがそもそもException出すので仕方ないという判断で。
			e.printStackTrace();
			return;
		}
	}

	private JDABuilder registDiscordEvent(JDABuilder d) {
		d.addEventListeners(new Event_Ready());
		d.addEventListeners(new Event_ServerLeave());
		return d;
	}

	private void scheduleTask() {
		new Task_AFK().runTaskTimerAsynchronously(this, 0L, 1200L);
		new Task_AutoRemoveTeam().runTaskTimer(this, 0L, 1200L);
	}

	public static void DiscordExceptionError(Class<?> clazz, MessageChannel channel, Throwable exception) {
		if (channel == null && ReportChannel != null) {
			channel = ReportChannel;
		} else if (channel == null) {
			System.out.println("DiscordExceptionError: channel == null and Main.ReportChannel == null.");
			System.out.println("DiscordExceptionError did not work properly!");
			return;
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		pw.flush();
		try {
			InputStream is = new ByteArrayInputStream(sw.toString().getBytes("utf-8"));
			channel.sendMessage(":pencil:おっと！MyMaid3のDiscord関連でなにか問題が発生したようです！ <@221991565567066112>\\n**ErrorMsg**: `"
					+ exception.getMessage()
					+ "`\n**Class**: `" + clazz.getName() + " (" + exception.getClass().getName() + ")`").queue();
			channel.sendFile(is, "stacktrace.txt").queue();
		} catch (UnsupportedEncodingException ex) {
			channel.sendMessage(":pencil:<@221991565567066112> おっと！メッセージ送信時に問題が発生したみたいです！\n**ErrorMsg**: `"
					+ ex.getMessage() + "`\n**Class**: `" + clazz.getName() + "`").queue();
		}
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

	public static void setJDA(JDA jda) {
		JDA = jda;
	}

	public static JDA getJDA() {
		return JDA;
	}

	public static MySQLDBManager getMySQLDBManager() {
		return MySQLDBManager;
	}
}
