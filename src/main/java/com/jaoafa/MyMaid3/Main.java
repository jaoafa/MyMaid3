package com.jaoafa.MyMaid3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.MyMaid3.Lib.ClassFinder;

public class Main extends JavaPlugin {
	private static JavaPlugin JavaPlugin = null;
	private static Main Main = null;

	/**
	 * プラグインが起動したときに呼び出し
	 * @author mine_book000
	 * @since 2019/08/20
	 */
	@Override
	public void onEnable() {
		setJavaPlugin(this);
		setMain(this);

		commandRegister();
		registEvent();
	}

	private void commandRegister() {
		try {
			ClassFinder classFinder = new ClassFinder(this.getClassLoader());
			for (Class<?> clazz : classFinder.findClasses("com.jaoafa.MyMaid3.Command")) {
				if (!clazz.getName().startsWith("com.jaoafa.MyMaid3.Command.Cmd_")) {
					continue;
				}
				String commandName = clazz.getName().substring("com.jaoafa.MyMaid3.Command.Cmd_".length())
						.toLowerCase();

				Constructor<?> construct = (Constructor<?>) clazz.getConstructor();
				Object instance = construct.newInstance();
				CommandPremise cmdPremise = (CommandPremise) instance;

				PluginCommand cmd = getCommand(commandName);
				cmd.setName(commandName);
				cmd.setDescription(cmdPremise.getDescription());
				cmd.setPermission("mymaid." + commandName);
				cmd.setUsage(cmdPremise.getUsage());

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

				Constructor<?> construct = (Constructor<?>) clazz.getConstructor();
				Object instance = construct.newInstance();

				if (instance instanceof Listener) {
					try {
						Listener listener = (Listener) instance;
						getServer().getPluginManager().registerEvents(listener, this);
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

	public static JavaPlugin getJavaPlugin() {
		return JavaPlugin;
	}

	public static void setJavaPlugin(JavaPlugin javaPlugin) {
		JavaPlugin = javaPlugin;
	}

	public static Main getMain() {
		return Main;
	}

	public static void setMain(Main main) {
		Main = main;
	}
}
