package me.nucha.teampvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.map.MapInfo;

public class CommandMaps implements CommandExecutor {

	private TeamPvP plugin;

	public CommandMaps(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("§2------------ §aMaps §2------------");
		int count = 1;
		for (MapInfo mapInfo : plugin.getMapInfos()) {
			String prefix = "§b";
			if (plugin.getMapManager().getCurrentMap().equalsIgnoreCase(mapInfo.getName())) {
				prefix = "§d§l";
			}
			String authorNames = mapInfo.getAuthors();
			if (!authorNames.isEmpty()) {
				authorNames = " §7by " + authorNames;
			}
			sender.sendMessage("§e" + (count++) + ". " + prefix + mapInfo.getName() + authorNames);
		}
		return true;
	}

}
