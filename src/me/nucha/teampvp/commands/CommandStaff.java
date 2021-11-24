package me.nucha.teampvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.staff.StaffManager;

public class CommandStaff implements CommandExecutor {

	private TeamPvP plugin;

	public CommandStaff(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Playerのみ実行できます");
			return true;
		}
		Player p = (Player) sender;
		if (!plugin.getTeamManager().getSpectators().contains(p)) {
			sender.sendMessage("§c観戦中のみ使用できます");
			return true;
		}
		StaffManager.setStaffMode(p, !StaffManager.isStaffMode(p));
		return true;
	}

}
