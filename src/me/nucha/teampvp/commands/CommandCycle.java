package me.nucha.teampvp.commands;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandCycle implements CommandExecutor {

	private TeamPvP plugin;

	public CommandCycle(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (MatchState.isState(MatchState.ENDING)) {
				plugin.getGameManager().startCycling();
			} else {
				sender.sendMessage("§c試合後の待機中ではありません");
			}
		}
		if (args.length == 1) {
			if (!MatchState.isState(MatchState.ENDING)) {
				sender.sendMessage("§c試合後の待機ではありません");
				return true;
			}
			if (!StringUtils.isNumeric(args[0])) {
				sender.sendMessage("§cカウントは数字で指定してください");
				sender.sendMessage("§c使い方: /cycle [秒数]");
				return true;
			}
			int count = Integer.valueOf(args[0]);
			if (count <= 0) {
				sender.sendMessage("§cカウントは1秒以上を指定してください");
				sender.sendMessage("§c使い方: /cycle [秒数]");
				return true;
			}
			plugin.getGameManager().startCycling(count);
		}
		return true;
	}
}
