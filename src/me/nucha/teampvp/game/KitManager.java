package me.nucha.teampvp.game;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.map.config.MapConfig;
import me.nucha.teampvp.map.item.ConfigItem;
import me.nucha.teampvp.map.item.KitItem;
import me.nucha.teampvp.map.region.Region;
import me.nucha.teampvp.utils.CustomItem;

public class KitManager {

	private Location spectatorSpawn;
	private HashMap<PvPTeam, Location> spawns;
	private List<KitItem> parentKit;
	private HashMap<PvPTeam, List<KitItem>> kitInventories;
	private List<KitItem> parentKitArmor;
	private HashMap<PvPTeam, List<KitItem>> kitArmors;
	private List<ConfigItem> killRewards;
	private boolean allowBuild;
	private boolean fallDamage;
	private boolean allowDamage;
	private List<Region> regions;
	private List<PvPTeam> teams;

	public KitManager(TeamPvP plugin, MapConfig mc) {
		spectatorSpawn = mc.getSpawnSpectator();
		spawns = new HashMap<>();
		parentKit = mc.getParentInventoryKit();
		kitInventories = new HashMap<>();
		parentKitArmor = mc.getParentArmorKit();
		kitArmors = new HashMap<>();
		allowBuild = mc.allowBuild();
		fallDamage = mc.fallDamage();
		allowDamage = mc.allowDamage();
		teams = mc.getTeams();
		killRewards = mc.getKillRewards();
		plugin.getTeamManager().unregisterAllTeam();
		for (PvPTeam team : teams) {
			plugin.getTeamManager().registerTeam(team.getColor(), team.getName(), team.getId(), team.getMax());
		}
		regions = mc.getRegions();
		for (PvPTeam team : plugin.getTeamManager().getTeams()) {
			spawns.put(team, mc.getSpawn(team));
			kitInventories.put(team, mc.getInventoryKit(team));
			kitArmors.put(team, mc.getArmorKit(team));
		}
	}

	public Location getSpectatorSpawn() {
		return spectatorSpawn;
	}

	public Location getSpawn(PvPTeam team) {
		return spawns.get(team);
	}

	public List<KitItem> getParentKit() {
		return parentKit;
	}

	public List<KitItem> getKitInventory(PvPTeam team) {
		return kitInventories.get(team);
	}

	public List<KitItem> getParentKitArmor() {
		return parentKitArmor;
	}

	public List<KitItem> getKitArmor(PvPTeam team) {
		return kitArmors.get(team);
	}

	public boolean allowBuild() {
		return allowBuild;
	}

	public boolean fallDamage() {
		return fallDamage;
	}

	public boolean allowDamage() {
		return allowDamage;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public List<PvPTeam> getTeams() {
		return teams;
	}

	public List<ConfigItem> getKillRewards() {
		return killRewards;
	}

	public void setSpectatorItem(Player p) {
		p.getInventory().clear();
		ItemStack air = new ItemStack(Material.AIR);
		p.getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
		ItemStack teleport = new CustomItem(Material.COMPASS, 1, "§cテレポートコンパス", "§7左クリックすると", "§7目の先にある奥のブロックに", "§7テレポートできます", "",
				"§7右クリックすると", "§7目の前にある壁の奥に", "§7テレポートできます");
		ItemStack teamSelector = new CustomItem(Material.NETHER_STAR, 1, "§aチーム選択", "§7右クリックすると", "§7チーム選択メニューが開けます");
		p.getInventory().setItem(0, teleport);
		p.getInventory().setItem(1, teamSelector);
		p.getInventory().setHeldItemSlot(1);
		p.updateInventory();
	}

}
