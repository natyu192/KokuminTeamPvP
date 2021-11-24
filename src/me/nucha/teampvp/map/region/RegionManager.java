package me.nucha.teampvp.map.region;

import java.util.ArrayList;
import java.util.List;

import me.nucha.teampvp.game.PvPTeam;

import org.bukkit.Location;

public class RegionManager {

	private List<Region> regions;

	public RegionManager() {
		regions = new ArrayList<>();
	}

	public Region createRegion(Location pos1, Location pos2, String name, boolean build, boolean enter) {
		if (isExistRegion(name)) {
			return getRegion(name);
		}
		Region region = new Region(pos1, pos2, name, build, enter);
		regions.add(region);
		return region;
	}

	public Region createRegion(RegionPos pos1, RegionPos pos2, String name, boolean build, boolean enter) {
		if (isExistRegion(name)) {
			return getRegion(name);
		}
		Region region = new Region(pos1, pos2, name, build, enter);
		regions.add(region);
		return region;
	}

	public TeamRegion createTeamRegion(Location pos1, Location pos2, String name, PvPTeam team, boolean build, boolean enter,
			String denyMessage) {
		if (isExistRegion(name)) {
			if (getRegion(name) instanceof TeamRegion)
				return (TeamRegion) getRegion(name);
		}
		TeamRegion region = new TeamRegion(pos1, pos2, name, team, build, enter, denyMessage);
		regions.add(region);
		return region;
	}

	public TeamRegion createTeamRegion(RegionPos pos1, RegionPos pos2, String name, PvPTeam team, boolean build, boolean enter,
			String denyMessage) {
		if (isExistRegion(name)) {
			if (getRegion(name) instanceof TeamRegion)
				return (TeamRegion) getRegion(name);
		}
		TeamRegion region = new TeamRegion(pos1, pos2, name, team, build, enter, denyMessage);
		regions.add(region);
		return region;
	}

	public void registerRegion(Region region) {
		if (isExistRegion(region.getName())) {
			return;
		}
		regions.add(region);
	}

	public void registerRegion(TeamRegion region) {
		if (isExistRegion(region.getName())) {
			return;
		}
		regions.add(region);
	}

	public void removeRegion(String name) {
		regions.remove(getRegion(name));
	}

	public void clearRegions() {
		regions.clear();
	}

	public Region getRegion(String name) {
		for (Region region : regions) {
			if (region.getName().equalsIgnoreCase(name)) {
				return region;
			}
		}
		return null;
	}

	public boolean isExistRegion(String name) {
		return getRegion(name) != null;
	}

	public List<Region> getRegions() {
		return regions;
	}

}
