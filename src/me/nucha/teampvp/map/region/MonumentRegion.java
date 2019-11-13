package me.nucha.teampvp.map.region;

import java.util.ArrayList;
import java.util.List;

import me.nucha.teampvp.game.PvPTeam;

import org.bukkit.Location;
import org.bukkit.Material;

public class MonumentRegion extends Region {

	private PvPTeam ownTeam;
	private List<Location> broken;
	private boolean destroyed;

	public MonumentRegion(Location pos1, Location pos2, String name, PvPTeam ownTeam) {
		super(pos1, pos2, name, true, true);
		this.ownTeam = ownTeam;
		getBlocks();
		broken = new ArrayList<Location>();
		destroyed = false;
	}

	public MonumentRegion(RegionPos pos1, RegionPos pos2, String name, PvPTeam ownTeam) {
		super(pos1, pos2, name, true, true);
		this.ownTeam = ownTeam;
		getBlocks();
		broken = new ArrayList<Location>();
		destroyed = false;
	}

	public PvPTeam getOwnTeam() {
		return ownTeam;
	}

	public void addBroken(Location l) {
		broken.add(l);
	}

	public boolean isBroken(Location l) {
		try {
			return broken.contains(l) || l.getBlock().getType() == Material.AIR && getDefaultBlocks().get(l) != Material.AIR;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean isBrokenAtLeastOnce() {
		for (Location l : getBlocks()) {
			if (isBroken(l)) {
				return true;
			}
		}
		return false;
	}

	public boolean destroyed() {
		return destroyed;
	}

	public int remains() {
		List<Location> blocks = getBlocks();
		int size = blocks.size();
		for (Location b : blocks) {
			try {
				if (isBroken(b) || (b.getBlock().getType() == Material.AIR && getDefaultBlocks().get(b) == Material.AIR)) {
					size--;
				}
			} catch (NullPointerException e) {
				size--;
			}
		}
		if (size <= 0) {
			destroyed = true;
		}
		return size;
	}

	/*public List<Location> getBlocks() {
		if (blocks != null)
			return blocks;
		List<Location> blocks = new ArrayList<>();
		RegionIterator regionIterator = new RegionIterator(this);
		World world = Bukkit.getWorld(TeamPvP.getInstance().getMapManager().getCurrentMap());
		for (RegionPos pos : regionIterator.getPoses()) {
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			blocks.add(new Location(world, x, y, z));
		}
		this.blocks = blocks;
		this.defaultBlocks = new HashMap<>();
		for (Location l : blocks) {
			defaultBlocks.put(l, l.getBlock());
		}
		return blocks;
	}*/

	public boolean isInBlock(Location l) {
		return getBlocks().contains(l);
	}
}
