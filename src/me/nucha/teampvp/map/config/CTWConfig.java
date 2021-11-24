package me.nucha.teampvp.map.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.objective.WoolObjective;
import me.nucha.teampvp.map.MapInfo;

public class CTWConfig extends MapConfig {

	public CTWConfig(FileConfiguration config) {
		super(config);
	}

	public CTWConfig(MapInfo mapInfo) {
		super(mapInfo);
	}

	public List<WoolObjective> getWoolObjectives() {
		List<WoolObjective> wools = new ArrayList<>();
		if (getConfig().isSet("wools")) {
			for (String sectionName : getConfig().getConfigurationSection("wools").getKeys(false)) {
				ConfigurationSection section = getConfig().getConfigurationSection("wools." + sectionName);
				PvPTeam team = TeamPvP.getInstance().getTeamManager().getTeamById(section.getString("team"));
				DyeColor color = DyeColor.valueOf(section.getString("color").toUpperCase());
				double x = section.getDouble("x");
				double y = section.getDouble("y");
				double z = section.getDouble("z");
				World world = Bukkit.getWorld(TeamPvP.getInstance().getMapManager().getTheWorldNameThatUsing());
				Location location = new Location(world, x, y, z);
				String wooldef = color.name().toUpperCase();
				String wool1 = wooldef.substring(0, 1);
				String wool2 = wooldef.substring(1).toLowerCase();
				String woolName = wool1 + wool2 + " Wool";
				WoolObjective woolObjective = new WoolObjective(woolName, TeamGameType.CTW, team, location, color);
				wools.add(woolObjective);
			}
		}
		return wools;
	}

}
