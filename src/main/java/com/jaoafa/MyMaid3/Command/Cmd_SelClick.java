package com.jaoafa.MyMaid3.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jaoafa.MyMaid3.Lib.CommandPremise;
import com.jaoafa.MyMaid3.Lib.MyMaidLibrary;
import com.jaoafa.MyMaid3.Lib.SelClickManager;

public class Cmd_SelClick extends MyMaidLibrary implements CommandExecutor, CommandPremise {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			SendMessage(sender, cmd, "このコマンドはゲーム内から実行してください。");
			return true;
		}
		Player player = (Player) sender;
		if (!isAM(player)) {
			SendMessage(sender, cmd, "あなたの権限ではこのコマンドを使用することはできません。");
			return true;
		}

		if (SelClickManager.isEnable(player)) {
			SelClickManager.setStatus(player, false);
			SendMessage(sender, cmd, "簡易SelectionClear機能を無効化しました。//selで手動クリアができます。");
		} else {
			SelClickManager.setStatus(player, true);
			SendMessage(sender, cmd, "簡易SelectionClear機能を有効化しました。走りながら右クリックすることで動作します。");
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "簡易SelectionClear機能の有効化・無効化ができます。";
	}

	@Override
	public List<String> getUsage() {
		return new ArrayList<String>() {
			{
				add("/selclick");
			}
		};
	}
}