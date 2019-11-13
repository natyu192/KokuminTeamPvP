package me.nucha.teampvp.commands;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandCancelCycle implements CommandExecutor {

	private TeamPvP plugin;

	public CommandCancelCycle(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (MatchState.isState(MatchState.CYCLING)) {
			plugin.getGameManager().cancelCycling();
		} else {
			sender.sendMessage("§cマップの移動中ではありません");
		}
		return true;
	}

}
