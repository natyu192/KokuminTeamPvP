package me.nucha.teampvp.map.config;

import me.nucha.teampvp.map.MapInfo;

import org.bukkit.configuration.file.FileConfiguration;

public class TDMConfig extends MapConfig {

	public TDMConfig(MapInfo mapInfo) {
		super(mapInfo);
	}

	public TDMConfig(FileConfiguration config) {
		super(config);
	}

	public int getMaxScore() {
		return getConfig().getInt("maxscore");
	}

}
