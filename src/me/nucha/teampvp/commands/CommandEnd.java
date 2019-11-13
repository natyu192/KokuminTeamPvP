package me.nucha.teampvp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;

public class CommandEnd implements CommandExecutor {

	private TeamPvP plugin;

	public CommandEnd(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (MatchState.isState(MatchState.IN_GAME)) {
			if (args.length == 1) {
				PvPTeam winner = plugin.getTeamManager().getTeamById(args[0]);
				if (winner == null) {
					sender.sendMessage("§c" + args[0] + "という ID のチームが存在しません");
					sender.sendMessage("§c使い方: /end [teamId]");
					return true;
				}
				List<PvPTeam> winners = new ArrayList<>();
				winners.add(winner);
				plugin.getGameManager().endGame(winners);
				sender.sendMessage("§9" + winner.getDisplayName() + " §eを勝利させました");
			} else {
				plugin.getGameManager().endGame(null);
				sender.sendMessage("§e試合を引き分けにしました");
			}
		} else {
			sender.sendMessage("§c試合中ではありません");
		}
		return true;
	}

}
