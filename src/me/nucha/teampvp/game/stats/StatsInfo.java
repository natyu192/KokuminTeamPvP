package me.nucha.teampvp.game.stats;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

public class StatsInfo {

	private FileConfiguration yml;
	private OfflinePlayer p;
	private int kills;
	private int deaths;
	private int wins;
	private int loses;
	private int monuments_broken;

	public StatsInfo(OfflinePlayer p, FileConfiguration yml) {
		this.yml = yml;
		this.p = p;
		this.kills = StatsCategory.KILLS.register(yml, p);
		this.deaths = StatsCategory.DEATHS.register(yml, p);
		this.wins = StatsCategory.WINS.register(yml, p);
		this.loses = StatsCategory.LOSES.register(yml, p);
		this.monuments_broken = StatsCategory.MONUMENTS_BROKEN.register(yml, p);
	}

	public int get(StatsCategory category) {
		switch (category) {
		case KILLS:
			return kills;
		case DEATHS:
			return deaths;
		case WINS:
			return wins;
		case LOSES:
			return loses;
		case MONUMENTS_BROKEN:
			return monuments_broken;
		default:
			return 0;
		}
	}

	public void set(StatsCategory category, int value) {
		switch (category) {
		case KILLS:
			this.kills = value;
		case DEATHS:
			this.deaths = value;
		case WINS:
			this.wins = value;
		case LOSES:
			this.loses = value;
		case MONUMENTS_BROKEN:
			this.monuments_broken = value;
		default:
			break;
		}
		yml.set(p.getUniqueId() + "." + category.getName(), value);
	}

	public void add(StatsCategory category, int value) {
		boolean matched = false;
		if (category == StatsCategory.KILLS) {
			this.kills += value;
			matched = true;
		} else if (category == StatsCategory.DEATHS) {
			this.deaths += value;
			matched = true;
		} else if (category == StatsCategory.WINS) {
			this.wins += value;
			matched = true;
		} else if (category == StatsCategory.LOSES) {
			this.loses += value;
			matched = true;
		} else if (category == StatsCategory.MONUMENTS_BROKEN) {
			this.monuments_broken += value;
			matched = true;
		}
		if (matched) {
			int before = get(category);
			yml.set(p.getUniqueId() + "." + category.getName(), before + value);
		}
	}

	public FileConfiguration getYml() {
		return yml;
	}
}
