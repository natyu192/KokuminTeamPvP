package me.nucha.teampvp.listeners.ctw;

import java.util.ArrayList;
import java.util.List;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.objective.WoolObjective;
import me.nucha.teampvp.map.MapManager;
import me.nucha.teampvp.map.config.CTWConfig;

public class CTWWoolManager {

	private TeamPvP plugin;
	private List<WoolObjective> woolObjectives;

	public CTWWoolManager(TeamPvP plugin) {
		this.plugin = plugin;
		this.woolObjectives = new ArrayList<>();
	}

	public List<WoolObjective> getWoolObjectives() {
		return woolObjectives;
	}

	public void load() {
		MapManager mapManager = plugin.getMapManager();
		CTWConfig config = (CTWConfig) mapManager.getCurrentMapInfo().getMapConfig();
		List<WoolObjective> woolObjectives = config.getWoolObjectives();
		this.woolObjectives = woolObjectives;
		for (WoolObjective woolObjective : woolObjectives) {
			plugin.getGameObjectiveManager().registerObjective(woolObjective);
		}
	}

}
