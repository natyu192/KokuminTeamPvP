package me.nucha.teampvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.map.MapInfo;

public class CommandSetNext implements CommandExecutor {

	private TeamPvP plugin;

	public CommandSetNext(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 1) {
			String mapName = args[0];
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					mapName += " " + args[i];
				}
			}
			MapInfo mapInfo = plugin.getMapManager().getMapInfoByName(mapName);
			if (mapInfo != null) {
				plugin.getMapManager().setNextMap(mapInfo.getName());
				Bukkit.broadcastMessage("\n      §d" + sender.getName() + " §bによって次のマップが " + mapInfo.getName() + " に指定されました\n ");
			} else {
				sender.sendMessage("§6" + mapName + " §cというマップが存在しません");
			}
			return true;
		}
		sender.sendMessage("§c使い方: /setnext <map name>");
		return true;
	}

}
