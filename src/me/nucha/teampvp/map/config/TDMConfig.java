package me.nucha.teampvp.map.config;

import org.bukkit.configuration.file.FileConfiguration;

import me.nucha.teampvp.map.MapInfo;

public class TDMConfig extends MapConfig {

	public TDMConfig(MapInfo mapInfo) {
		super(mapInfo);
	}

	public TDMConfig(FileConfiguration config) {
		super(config);
	}

	public int getMaxScore() {
		if (getConfig().isSet("maxscore")) {
			return getConfig().getInt("maxscore");
		} else {
			return 100;
		}
	}

	public int getTime() {
		if (getConfig().isSet("time")) {
			return getConfig().getInt("time");
		} else {
			return 300;
		}
	}

}
