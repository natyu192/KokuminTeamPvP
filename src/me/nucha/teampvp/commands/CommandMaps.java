package me.nucha.teampvp.commands;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.map.MapInfo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandMaps implements CommandExecutor {

	private TeamPvP plugin;

	public CommandMaps(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("§2------------ §aMaps §2------------");
		for (MapInfo mapInfo : plugin.getMapInfos()) {
			String prefix = "§b";
			if (plugin.getMapManager().getCurrentMap().equalsIgnoreCase(mapInfo.getName())) {
				prefix = "§d§l";
			}
			sender.sendMessage(prefix + mapInfo.getName() + " §8(§e" + mapInfo.getMapConfig().getTeamGameType().getDisplayName() + "§8)");
		}
		return true;
	}

}
