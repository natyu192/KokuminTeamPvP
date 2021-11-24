package me.nucha.teampvp.map.region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import me.nucha.teampvp.TeamPvP;

public class Region {

	private RegionPos pos1;
	private RegionPos pos2;
	private String name;
	private boolean build;
	private boolean enter;
	public List<Player> containPlayers;
	private List<Location> blocks;
	private HashMap<Location, Material> defaultBlocks;

	public Region(RegionPos pos1, RegionPos pos2, String name, boolean build, boolean enter) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.name = name;
		this.containPlayers = new ArrayList<>();
		this.build = build;
		this.enter = enter;
		this.defaultBlocks = new HashMap<>();
	}

	public Region(Location pos1, Location pos2, String name, boolean build, boolean enter) {
		RegionPos rpos1 = new RegionPos(pos1.getX(), pos1.getY(), pos1.getZ());
		RegionPos rpos2 = new RegionPos(pos2.getX(), pos2.getY(), pos2.getZ());
		this.pos1 = rpos1;
		this.pos2 = rpos2;
		this.name = name;
		this.containPlayers = new ArrayList<>();
		this.build = build;
		this.enter = enter;
		this.defaultBlocks = new HashMap<>();
	}

	public boolean isIn(Location l) {
		double x = l.getX();
		double y = l.getY();
		double z = l.getZ();
		return isInAABB(x, y, z);
	}

	public boolean isIn(Block b) {
		Location l = b.getLocation();
		double x = l.getBlockX() + 0.5;
		double y = l.getBlockY() + 0.5;
		double z = l.getBlockZ() + 0.5;
		return isInAABB(x, y, z);
	}

	public boolean isIn(Player p) {
		return isIn(p.getLocation());
	}

	public boolean isIn(Vector vector) {
		double x = vector.getX();
		double y = vector.getY();
		double z = vector.getZ();
		return isInAABB(x, y, z);
	}

	public boolean isIn(RegionPos pos) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		return isInAABB(x, y, z);
	}

	public boolean isInAABB(double x, double y, double z) {
		double minX = Math.min(pos1.getX(), pos2.getX());
		double minY = Math.min(pos1.getY(), pos2.getY());
		double minZ = Math.min(pos1.getZ(), pos2.getZ());
		double maxX = Math.max(pos1.getX(), pos2.getX());
		double maxY = Math.max(pos1.getY(), pos2.getY());
		double maxZ = Math.max(pos1.getZ(), pos2.getZ());
		return x >= minX && x < maxX + 1
				&& y >= minY && y < maxY + 1
				&& z >= minZ && z < maxZ + 1;
		/*double x1 = pos1.getX();
		double y1 = pos1.getY();
		double z1 = pos1.getZ();
		double x2 = pos2.getX();
		double y2 = pos2.getY();
		double z2 = pos2.getZ();*/
		/*if (x < 0)
			x--;
		if (y < 0)
			y--;
		if (z < 0)
			z--;*/
		/*if (x >= Math.max(x1, x2))
			return false;
		if (x <= Math.min(x1, x2))
			return false;
		if (y >= Math.max(y1, y2))
			return false;
		if (y <= Math.min(y1, y2))
			return false;
		if (z >= Math.max(z1, z2))
			return false;
		if (z <= Math.min(z1, z2))
			return false;
		return true;*/
	}

	/*public boolean isInAABB(int x, int y, int z) {
		double x1 = pos1.getX();
		double y1 = pos1.getY();
		double z1 = pos1.getZ();
		double x2 = pos2.getX();
		double y2 = pos2.getY();
		double z2 = pos2.getZ();
		x1 = Math.floor(x1);
		y1 = Math.floor(y1);
		z1 = Math.floor(z1);
		x2 = Math.floor(x2);
		y2 = Math.floor(y2);
		z2 = Math.floor(z2);
		if (x >= x1)
			return false;
		if (x <= x2)
			return false;
		if (y >= y1)
			return false;
		if (y <= y2)
			return false;
		if (z >= z1)
			return false;
		if (z <= z2)
			return false;
		return true;
	}*/

	public boolean contains(RegionPos position) {
		double x = position.getX();
		double y = position.getY();
		double z = position.getZ();

		RegionPos min = pos1;
		RegionPos max = pos2;

		double x1 = NumberConversions.floor(min.getX());
		double y1 = NumberConversions.floor(min.getY());
		double z1 = NumberConversions.floor(min.getZ());
		double x2 = NumberConversions.floor(max.getX());
		double y2 = NumberConversions.floor(max.getY());
		double z2 = NumberConversions.floor(max.getZ());

		return x >= Math.max(x1, x2) && x <= Math.min(x1, x2) && y >= Math.max(y1, y2) && y <= Math.min(y1, y2) && z >= Math.max(z1, z2)
				&& z <= Math.min(z1, z2);
	}

	public boolean canBuild() {
		return build;
	}

	public boolean canEnter() {
		return enter;
	}

	public String getName() {
		return name;
	}

	public RegionPos getPos1() {
		return pos1;
	}

	public RegionPos getPos2() {
		return pos2;
	}

	public int size() {
		double x1 = pos1.getX();
		double y1 = pos1.getX();
		double z1 = pos1.getX();
		double x2 = pos2.getX();
		double y2 = pos2.getX();
		double z2 = pos2.getX();
		return (int) ((Math.max(x1, x2) - Math.min(x1, x2) + 1) * Math.max(y1, y2) - Math.min(y1, y2) * Math.max(z1, z2) - Math.min(z1, z2) + 1);
	}

	public List<Location> getBlocks() {
		if (this.blocks != null) {
			return this.blocks;
		}
		defaultBlocks.clear();
		List<Location> blocks = new ArrayList<>();
		World world = Bukkit.getWorld(TeamPvP.getInstance().getMapManager().getTheWorldNameThatUsing());
		int minx = NumberConversions.floor(Math.min(pos1.getX(), pos2.getX()));
		int miny = NumberConversions.floor(Math.min(pos1.getY(), pos2.getY()));
		int minz = NumberConversions.floor(Math.min(pos1.getZ(), pos2.getZ()));
		int maxx = NumberConversions.floor(Math.max(pos1.getX(), pos2.getX()));
		int maxy = NumberConversions.floor(Math.max(pos1.getY(), pos2.getY()));
		int maxz = NumberConversions.floor(Math.max(pos1.getZ(), pos2.getZ()));
		int x = minx;
		int y = miny;
		int z = minz;
		while (y <= maxy) {
			while (x <= maxx) {
				while (z <= maxz) {
					Location l = new Location(world, x, y, z);
					if (!blocks.contains(l)) {
						blocks.add(l);
						defaultBlocks.put(l, l.getBlock().getType());
					}
					z++;
				}
				z = minz;
				Location l = new Location(world, x, y, z);
				if (!blocks.contains(l)) {
					blocks.add(l);
					defaultBlocks.put(l, l.getBlock().getType());
				}
				x++;
			}
			x = minx;
			z = minz;
			Location l = new Location(world, x, y, z);
			if (!blocks.contains(l)) {
				blocks.add(l);
				defaultBlocks.put(l, l.getBlock().getType());
			}
			y++;
			x = minx;
			z = minz;
		}
		this.blocks = blocks;
		return blocks;
	}

	public HashMap<Location, Material> getDefaultBlocks() {
		return defaultBlocks;
	}

}
