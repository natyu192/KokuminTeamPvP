package me.nucha.teampvp.commands;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandCancelStart implements CommandExecutor {

	private TeamPvP plugin;

	public CommandCancelStart(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (MatchState.isState(MatchState.COUNTDOWN)) {
			plugin.getGameManager().cancelStart();
		} else {
			sender.sendMessage("§cカウント中ではありません");
		}
		return true;
	}

}
