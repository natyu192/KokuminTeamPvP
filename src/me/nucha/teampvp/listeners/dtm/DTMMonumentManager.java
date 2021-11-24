package me.nucha.teampvp.listeners.dtm;

import java.util.ArrayList;
import java.util.List;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.objective.MonumentObjective;
import me.nucha.teampvp.map.MapManager;
import me.nucha.teampvp.map.config.DTMConfig;
import me.nucha.teampvp.map.region.MonumentRegion;

public class DTMMonumentManager {

	private TeamPvP plugin;
	private List<MonumentRegion> monumentRegions;

	public DTMMonumentManager(TeamPvP plugin) {
		this.plugin = plugin;
		monumentRegions = new ArrayList<>();
	}

	public void load() {
		MapManager mapManager = plugin.getMapManager();
		DTMConfig config = (DTMConfig) mapManager.getCurrentMapInfo().getMapConfig();
		List<MonumentRegion> regions = config.getMonumentRegions();
		monumentRegions = regions;
		for (MonumentRegion region : regions) {
			PvPTeam ownTeam = region.getOwnTeam();
			// PvPTeam ownTeam = region.getOwnTeam() == PvPTeam.RED ? PvPTeam.BLUE : PvPTeam.RED;
			// DTMMonumentListener.java
			MonumentObjective obj = new MonumentObjective(region.getName(), TeamGameType.DTM, ownTeam, region);
			plugin.getGameObjectiveManager().registerObjective(obj);
		}
	}

	public List<MonumentRegion> getMonumentRegions() {
		return monumentRegions;
	}

}
