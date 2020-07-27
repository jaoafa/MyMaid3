package com.jaoafa.MyMaid3.Lib;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class MyMaidConfig {
	private static JDA JDA = null;
	private static MySQLDBManager MySQLDBManager = null;
	private static TextChannel ReportChannel = null;
	private static TextChannel ServerChatChannel = null;
	private static TextChannel jaotanChannel = null;
	private static String BugReportWebhookUrl = null;
	private static String FeedbackWebhookUrl = null;
	private static Map<UUID, BukkitTask> coOLDEnabler = new HashMap<>();
	private static Map<UUID, Location> coOLDLoc = new HashMap<>();
	private static MySQLDBManager MySQLDBManager_COOLD = null;

	public static JDA getJDA() {
		return JDA;
	}

	public static void setJDA(JDA jDA) {
		JDA = jDA;
	}

	public static MySQLDBManager getMySQLDBManager() {
		return MySQLDBManager;
	}

	public static void setMySQLDBManager(MySQLDBManager mySQLDBManager) {
		MySQLDBManager = mySQLDBManager;
	}

	public static TextChannel getReportChannel() {
		return ReportChannel;
	}

	public static void setReportChannel(TextChannel reportChannel) {
		ReportChannel = reportChannel;
	}

	public static TextChannel getServerChatChannel() {
		return ServerChatChannel;
	}

	public static void setServerChatChannel(TextChannel serverChatChannel) {
		ServerChatChannel = serverChatChannel;
	}

	public static TextChannel getJaotanChannel() {
		return jaotanChannel;
	}

	public static void setJaotanChannel(TextChannel jaotanChannel) {
		MyMaidConfig.jaotanChannel = jaotanChannel;
	}

	public static String getBugReportWebhookUrl() {
		return BugReportWebhookUrl;
	}

	public static void setBugReportWebhookUrl(String bugReportWebhookUrl) {
		BugReportWebhookUrl = bugReportWebhookUrl;
	}

	public static String getFeedbackWebhookUrl() {
		return FeedbackWebhookUrl;
	}

	public static void setFeedbackWebhookUrl(String feedbackWebhookUrl) {
		FeedbackWebhookUrl = feedbackWebhookUrl;
	}

	public static Map<UUID, BukkitTask> getCoOLDEnabler() {
		return coOLDEnabler;
	}

	public static void setCoOLDEnabler(Map<UUID, BukkitTask> coOLDEnabler) {
		MyMaidConfig.coOLDEnabler = coOLDEnabler;
	}

	public static BukkitTask putCoOLDEnabler(UUID uuid, BukkitTask task) {
		return MyMaidConfig.coOLDEnabler.put(uuid, task);
	}

	public static BukkitTask removeCoOLDEnabler(UUID uuid) {
		return MyMaidConfig.coOLDEnabler.remove(uuid);
	}

	public static Map<UUID, Location> getCoOLDLoc() {
		return coOLDLoc;
	}

	public static void setCoOLDLoc(Map<UUID, Location> coOLDLoc) {
		MyMaidConfig.coOLDLoc = coOLDLoc;
	}

	public static Location putCoOLDLoc(UUID uuid, Location loc) {
		return MyMaidConfig.coOLDLoc.put(uuid, loc);
	}

	public static MySQLDBManager getMySQLDBManager_COOLD() {
		return MySQLDBManager_COOLD;
	}

	public static void setMySQLDBManager_COOLD(MySQLDBManager mySQLDBManager_COOLD) {
		MySQLDBManager_COOLD = mySQLDBManager_COOLD;
	}

}
