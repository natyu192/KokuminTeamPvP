package me.nucha.teampvp.map;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.map.config.AnniConfig;
import me.nucha.teampvp.map.config.CTWConfig;
import me.nucha.teampvp.map.config.DTMConfig;
import me.nucha.teampvp.map.config.MapConfig;
import me.nucha.teampvp.map.config.TDMConfig;
import me.nucha.teampvp.utils.UUIDUtils;

public class MapInfo {

	private File parentFile;
	private File worldFile;
	private FileConfiguration config;
	private String name;

	public MapInfo(File parentFile, TeamPvP plugin) {
		this.parentFile = parentFile;
		File configFile = new File(parentFile + "/config.yml");
		this.config = YamlConfiguration.loadConfiguration(configFile);
		this.name = config.getString("name");
		this.worldFile = plugin.getFileByNameFromFile(parentFile, this.name);
	}

	public MapInfo(File parentFile, File configFile, File worldFile, TeamPvP plugin) {
		this.parentFile = parentFile;
		this.config = YamlConfiguration.loadConfiguration(configFile);
		this.name = config.getString("name");
		this.worldFile = plugin.getFileByNameFromFile(parentFile, this.name);
	}

	public File getParentFile() {
		return parentFile;
	}

	public File getWorldFile() {
		return worldFile;
	}

	public void setConfigFile(File file) {
		this.config = YamlConfiguration.loadConfiguration(file);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public String getName() {
		return name;
	}

	public String getAuthors() {
		String authorNames = "";
		if (getMapConfig().getAuthors() != null) {
			int a = 1;
			for (String uuid : getMapConfig().getAuthors()) {
				String name = "§a" + UUIDUtils.getName(uuid);
				if (authorNames.isEmpty()) {
					authorNames = name;
				} else {
					if (a == getMapConfig().getAuthors().size()) {
						authorNames += " §2and §a" + name;
					} else {
						authorNames += "§2, §a" + name;
					}
				}
				a++;
			}
		}
		return authorNames;
	}

	public MapConfig getMapConfig() {
		MapConfig config = new MapConfig(this);
		if (config.getTeamGameType() == TeamGameType.TDM) {
			return new TDMConfig(this);
		}
		if (config.getTeamGameType() == TeamGameType.DTM) {
			return new DTMConfig(this);
		}
		if (config.getTeamGameType() == TeamGameType.CTW) {
			return new CTWConfig(this);
		}
		if (config.getTeamGameType() == TeamGameType.ANNI) {
			return new AnniConfig(this);
		}
		return config;
	}

}
