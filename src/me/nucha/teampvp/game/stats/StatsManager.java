package me.nucha.teampvp.game.stats;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import me.nucha.teampvp.TeamPvP;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StatsManager {

	private File statsFile;
	private FileConfiguration stats;
	private HashMap<OfflinePlayer, StatsInfo> statsInfos;

	public StatsManager(TeamPvP plugin) {
		File statsFile = new File(plugin.getDataFolder() + "/stats.yml");
		if (!statsFile.exists()) {
			try {
				statsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.statsFile = statsFile;
		this.stats = YamlConfiguration.loadConfiguration(statsFile);
		this.statsInfos = new HashMap<>();
	}

	public void putStats(OfflinePlayer p) {
		StatsInfo statsInfo = new StatsInfo(p, stats);
		statsInfos.put(p, statsInfo);
	}

	public void removeStats(OfflinePlayer p) {
		statsInfos.remove(p);
	}

	public StatsInfo getStatsInfo(OfflinePlayer p) {
		return statsInfos.get(p);
	}

	public void save() {
		try {
			stats.save(statsFile);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public FileConfiguration getStatsYml() {
		return stats;
	}

	public File getStatsFile() {
		return statsFile;
	}

}
