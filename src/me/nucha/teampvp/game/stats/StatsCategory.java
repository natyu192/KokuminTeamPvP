package me.nucha.teampvp.game.stats;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

public enum StatsCategory {
	KILLS("kills", "キル数"),
	DEATHS("deaths", "死亡数"),
	WINS("wins", "勝利数"),
	LOSES("loses", "敗北数"),
	MONUMENTS_BROKEN("monuments-broken", "モニュメント破壊数"),
	WOOLS_PLACED("wools-placed", "羊毛設置数");

	private String name;
	private String displayName;

	private StatsCategory(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int register(FileConfiguration yml, OfflinePlayer p) {
		if (yml.isSet(p.getUniqueId().toString() + "." + name)) {
			return yml.getInt(p.getUniqueId().toString() + "." + name);
		} else {
			return 0;
		}
	}
}
