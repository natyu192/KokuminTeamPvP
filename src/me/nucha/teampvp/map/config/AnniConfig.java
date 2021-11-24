package me.nucha.teampvp.map.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.objective.NexusObjective;
import me.nucha.teampvp.map.MapInfo;

public class AnniConfig extends MapConfig {

	public AnniConfig(FileConfiguration config) {
		super(config);
	}

	public AnniConfig(MapInfo mapInfo) {
		super(mapInfo);
	}

	public List<Location> getEnderFurnaces() {
		List<Location> furnaces = new ArrayList<>();
		if (getConfig().isSet("ender-furnaces")) {
			for (String sectionName : getConfig().getConfigurationSection("ender-furnaces").getKeys(false)) {
				ConfigurationSection section = getConfig().getConfigurationSection("ender-furnaces." + sectionName);
				double x = section.getDouble("x");
				double y = section.getDouble("y");
				double z = section.getDouble("z");
				World world = Bukkit.getWorld(TeamPvP.getInstance().getMapManager().getTheWorldNameThatUsing());
				Location location = new Location(world, x, y, z);
				furnaces.add(location);
			}
		}
		return furnaces;
	}

	public List<NexusObjective> getNexuses() {
		List<NexusObjective> nexuses = new ArrayList<>();
		if (getConfig().isSet("nexuses")) {
			for (String sectionName : getConfig().getConfigurationSection("nexuses").getKeys(false)) {
				ConfigurationSection section = getConfig().getConfigurationSection("nexuses." + sectionName);
				PvPTeam team = TeamPvP.getInstance().getTeamManager().getTeamById(section.getString("team"));
				int hp = 75;
				if (section.isSet("hp")) {
					hp = section.getInt("hp");
				}
				double x = section.getDouble("x");
				double y = section.getDouble("y");
				double z = section.getDouble("z");
				World world = Bukkit.getWorld(TeamPvP.getInstance().getMapManager().getTheWorldNameThatUsing());
				Location location = new Location(world, x, y, z);
				NexusObjective obj = new NexusObjective(team.getDisplayName(), team, location, hp);
				nexuses.add(obj);
			}
		}
		return nexuses;
	}

}
