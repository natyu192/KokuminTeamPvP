package me.nucha.teampvp.map.region;

import me.nucha.teampvp.game.PvPTeam;

import org.bukkit.Location;

public class TeamRegion extends Region {

	private PvPTeam ownTeam;
	private String denyMessage;

	public TeamRegion(Location pos1, Location pos2, String name, PvPTeam ownTeam, boolean build, boolean enter, String denyMessage) {
		super(pos1, pos2, name, build, enter);
		this.ownTeam = ownTeam;
		this.denyMessage = denyMessage;
	}

	public TeamRegion(RegionPos pos1, RegionPos pos2, String name, PvPTeam ownTeam, boolean build, boolean enter, String denyMessage) {
		super(pos1, pos2, name, build, enter);
		this.ownTeam = ownTeam;
		this.denyMessage = denyMessage;
	}

	public PvPTeam getOwnTeam() {
		return ownTeam;
	}

	public String getDenyMessage() {
		return denyMessage;
	}

}
