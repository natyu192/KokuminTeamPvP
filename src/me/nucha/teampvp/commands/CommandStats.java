package me.nucha.teampvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.stats.StatsCategory;
import me.nucha.teampvp.game.stats.StatsInfo;
import me.nucha.teampvp.game.stats.StatsManager;

public class CommandStats implements CommandExecutor {

	private TeamPvP plugin;

	public CommandStats(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			Player t = Bukkit.getPlayer(args[0]);
			if (t != null) {
				StatsManager statsManager = plugin.getStatsManager();
				StatsInfo statsInfo = statsManager.getStatsInfo(t);
				sender.sendMessage("§2------------ §e" + t.getDisplayName() + " §aの戦績 §2------------");
				for (StatsCategory category : StatsCategory.values()) {
					sender.sendMessage("§a" + category.getDisplayName() + "§8: §e" + statsInfo.get(category));
				}
			} else {
				Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
					OfflinePlayer ot = Bukkit.getOfflinePlayer(args[0]);
					if (ot.hasPlayedBefore()) {
						StatsManager statsManager = plugin.getStatsManager();
						StatsInfo statsInfo = new StatsInfo(ot, statsManager.getStatsYml());
						sender.sendMessage("§2------------ §e" + ot.getName() + "§7(オフライン) §aの戦績 §2------------");
						for (StatsCategory category : StatsCategory.values()) {
							sender.sendMessage("§a" + category.getDisplayName() + "§8: §e" + statsInfo.get(category));
						}
					} else {
						sender.sendMessage("§6" + args[1] + " §cというプレイヤーが見つかりませんでした");
					}
				});
			}
			return true;
		}
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		StatsManager statsManager = plugin.getStatsManager();
		StatsInfo statsInfo = statsManager.getStatsInfo(p);
		sender.sendMessage("§2------------ §eあなた §aの戦績 §2------------");
		for (StatsCategory category : StatsCategory.values()) {
			sender.sendMessage("§a" + category.getDisplayName() + "§8: §e" + statsInfo.get(category));
		}
		return true;
	}

}
