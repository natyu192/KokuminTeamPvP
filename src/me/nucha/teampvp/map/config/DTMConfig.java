package me.nucha.teampvp.map.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.map.MapInfo;
import me.nucha.teampvp.map.region.MonumentRegion;
import me.nucha.teampvp.map.region.RegionPos;

public class DTMConfig extends MapConfig {

	public DTMConfig(MapInfo mapInfo) {
		super(mapInfo);
	}

	public DTMConfig(FileConfiguration config) {
		super(config);
	}

	public List<MonumentRegion> getMonumentRegions() {
		List<MonumentRegion> regions = new ArrayList<>();
		if (getConfig().isSet("monuments")) {
			for (String sectionName : getConfig().getConfigurationSection("monuments").getKeys(false)) {
				ConfigurationSection section = getConfig().getConfigurationSection("monuments." + sectionName);
				PvPTeam team = TeamPvP.getInstance().getTeamManager().getTeamById(section.getString("team"));
				// DTMMonumentListener.java
				String name = section.isSet("name") ? section.getString("name") : sectionName;
				double x1 = section.getDouble("pos1.x");
				double y1 = section.getDouble("pos1.y");
				double z1 = section.getDouble("pos1.z");
				double x2 = section.getDouble("pos2.x");
				double y2 = section.getDouble("pos2.y");
				double z2 = section.getDouble("pos2.z");
				RegionPos pos1 = new RegionPos(x1, y1, z1);
				RegionPos pos2 = new RegionPos(x2, y2, z2);
				MonumentRegion region = new MonumentRegion(pos1, pos2, name, team);
				regions.add(region);
			}
		}
		return regions;
	}

}
