package me.nucha.teampvp.listeners.anni;

import java.util.ArrayList;
import java.util.List;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.objective.NexusObjective;
import me.nucha.teampvp.map.MapManager;
import me.nucha.teampvp.map.config.AnniConfig;

import org.bukkit.Location;

public class AnniLocationManager {

	private TeamPvP plugin;
	private List<NexusObjective> nexusObjectives;
	private List<Location> furnaceLocations;

	public AnniLocationManager(TeamPvP plugin) {
		this.plugin = plugin;
		this.nexusObjectives = new ArrayList<>();
		this.furnaceLocations = new ArrayList<>();
	}

	public List<NexusObjective> getNexusObjectives() {
		return nexusObjectives;
	}

	public List<Location> getFurnaceLocations() {
		return furnaceLocations;
	}

	public void load() {
		MapManager mapManager = plugin.getMapManager();
		AnniConfig config = (AnniConfig) mapManager.getCurrentMapInfo().getMapConfig();
		List<NexusObjective> nexusObjectives = config.getNexuses();
		this.nexusObjectives = nexusObjectives;
		for (NexusObjective nexusObjective : nexusObjectives) {
			plugin.getGameObjectiveManager().registerObjective(nexusObjective);
		}
		this.furnaceLocations = config.getEnderFurnaces();
	}

}
