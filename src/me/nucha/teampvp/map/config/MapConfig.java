package me.nucha.teampvp.map.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.map.MapInfo;
import me.nucha.teampvp.map.MapTutorial;
import me.nucha.teampvp.map.item.ConfigItem;
import me.nucha.teampvp.map.item.KitItem;
import me.nucha.teampvp.map.region.Region;
import me.nucha.teampvp.map.region.RegionPos;
import me.nucha.teampvp.map.region.TeamRegion;

public class MapConfig {

	private FileConfiguration config;

	public MapConfig(FileConfiguration config) {
		this.config = config;
	}

	public MapConfig(MapInfo mapInfo) {
		this.config = mapInfo.getConfig();
	}

	public List<String> getAuthors() {
		if (config.isSet("authors")) {
			return config.getStringList("authors");
		}
		return null;
	}

	public List<String> getDescription() {
		if (config.isSet("description")) {
			return config.getStringList("description");
		}
		return null;
	}

	public String getName() {
		return config.getString("name");
	}

	public TeamGameType getTeamGameType() {
		return TeamGameType.valueOf(config.getString("gametype"));
	}

	public boolean allowBuild() {
		if (config.isSet("allow-build")) {
			return config.getBoolean("allow-build");
		}
		return true;
	}

	public boolean fallDamage() {
		if (config.isSet("fall-damage")) {
			return config.getBoolean("fall-damage");
		}
		return true;
	}

	public boolean allowDamage() {
		if (config.isSet("allow-damage")) {
			return config.getBoolean("allow-damage");
		}
		return true;
	}

	public int getNoDamageRadius() {
		if (config.isSet("no-damage-radius")) {
			return config.getInt("no-damage-radius");
		}
		return 3;
	}

	public List<Region> getRegions() {
		List<Region> regions = new ArrayList<>();
		if (config.isSet("regions")) {
			for (String sectionName : config.getConfigurationSection("regions").getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection("regions." + sectionName);
				PvPTeam team = TeamPvP.getInstance().getTeamManager().getTeamById(section.getString("team"));
				String name = section.isSet("name") ? section.getString("name") : sectionName;
				String denyMessage = section.isSet("deny-message") ? section.getString("deny-message") : "そのエリアには入れません";
				boolean build = section.isSet("build") ? section.getBoolean("build") : false;
				boolean enter = section.isSet("enter") ? section.getBoolean("enter") : true;
				double x1 = section.getDouble("pos1.x");
				double y1 = section.getDouble("pos1.y");
				double z1 = section.getDouble("pos1.z");
				double x2 = section.getDouble("pos2.x");
				double y2 = section.getDouble("pos2.y");
				double z2 = section.getDouble("pos2.z");
				RegionPos pos1 = new RegionPos(x1, y1, z1);
				RegionPos pos2 = new RegionPos(x2, y2, z2);
				TeamRegion region = new TeamRegion(pos1, pos2, name, team, build, enter, denyMessage);
				regions.add(region);
			}
		}
		return regions;
	}

	public List<MapTutorial> getTutorials() {
		List<MapTutorial> tutorials = new ArrayList<>();
		if (config.isSet("tutorial")) {
			tutorials.add(new MapTutorial(null,
					Arrays.asList(new String[] { "&aこのマップにはチュートリアルがあります", "&aこの本を&e右クリック&aで次のページへ、&e左クリック&aで", "&a前のページへ移動することができます。" })));
			for (String sectionName : config.getConfigurationSection("tutorial").getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection("tutorial." + sectionName);
				Location teleport = null;
				if (section.isSet("teleport")) {
					double x = section.getDouble("teleport.x");
					double y = section.getDouble("teleport.y");
					double z = section.getDouble("teleport.z");
					float yaw = (float) section.getDouble("teleport.yaw");
					float pitch = (float) section.getDouble("teleport.pitch");
					World world = Bukkit.getWorld(TeamPvP.getInstance().getMapManager().getTheWorldNameThatUsing());
					teleport = new Location(world, x, y, z, yaw, pitch);
				}
				List<String> messages = new ArrayList<>();
				if (section.isSet("messages")) {
					for (String message : section.getStringList("messages")) {
						messages.add(message);
					}
				}
				tutorials.add(new MapTutorial(teleport, messages));
				TeamPvP.sendConsoleMessage("Tutorial loaded: " + section.getCurrentPath());
			}
			tutorials.add(new MapTutorial(null,
					Arrays.asList(new String[] { "&dチュートリアルが終了しました！さっそくゲームへ参加してみましょう！", "&a手持ちの&fネザースター&aをクリックすると&bチーム選択メニュー&aが表示されます" })));
		}
		return tutorials;
	}

	public List<PvPTeam> getTeams() {
		List<PvPTeam> teams = new ArrayList<>();
		if (config.isSet("teams")) {
			for (String sectionName : config.getConfigurationSection("teams").getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection("teams." + sectionName);
				String name = section.getString("name");
				int max = section.getInt("max");
				ChatColor color = ChatColor.valueOf(section.getString("color").toUpperCase());
				PvPTeam team = new PvPTeam(color, name, sectionName, max);
				teams.add(team);
			}
		}
		return teams;
	}

	public Location getSpawnSpectator() {
		double x = config.getDouble("location.spectator.x");
		double y = config.getDouble("location.spectator.y");
		double z = config.getDouble("location.spectator.z");
		float yaw = (float) config.getDouble("location.spectator.yaw");
		float pitch = (float) config.getDouble("location.spectator.pitch");
		World world = Bukkit.getWorld(TeamPvP.getInstance().getMapManager().getTheWorldNameThatUsing());
		return new Location(world, x, y, z, yaw, pitch);
	}

	public Location getSpawn(PvPTeam team) {
		String teamId = team.getId();
		if (teamId.equals("spectator")) {
			return null;
		}
		double x = config.getDouble("location." + teamId + ".x");
		double y = config.getDouble("location." + teamId + ".y");
		double z = config.getDouble("location." + teamId + ".z");
		float yaw = (float) config.getDouble("location." + teamId + ".yaw");
		float pitch = (float) config.getDouble("location." + teamId + ".pitch");
		World world = Bukkit.getWorld(TeamPvP.getInstance().getMapManager().getTheWorldNameThatUsing());
		return new Location(world, x, y, z, yaw, pitch);
	}

	public List<KitItem> getParentInventoryKit() {
		List<KitItem> kitItems = new ArrayList<KitItem>();
		ConfigurationSection section = config.getConfigurationSection("kits.parent.inventory");
		if (section != null) {
			for (String fc : section.getKeys(false)) {
				KitItem kitItem = new KitItem(config.getConfigurationSection("kits.parent.inventory." + fc));
				kitItems.add(kitItem);
			}
		}
		return kitItems;
	}

	public List<ConfigItem> getKillRewards() {
		List<ConfigItem> kitItems = new ArrayList<ConfigItem>();
		ConfigurationSection section = config.getConfigurationSection("kits.kill-rewards");
		if (section != null) {
			for (String fc : section.getKeys(false)) {
				KitItem kitItem = new KitItem(config.getConfigurationSection("kits.kill-rewards." + fc));
				kitItems.add(kitItem);
			}
		}
		return kitItems;
	}

	public List<KitItem> getParentArmorKit() {
		List<KitItem> kitItems = new ArrayList<>();
		ConfigurationSection section = config.getConfigurationSection("kits.parent.armor");
		if (section != null) {
			if (section.isSet("helmet"))
				kitItems.add(new KitItem(section.getConfigurationSection("helmet")));
			if (section.isSet("chestplate"))
				kitItems.add(new KitItem(section.getConfigurationSection("chestplate")));
			if (section.isSet("leggings"))
				kitItems.add(new KitItem(section.getConfigurationSection("leggings")));
			if (section.isSet("boots"))
				kitItems.add(new KitItem(section.getConfigurationSection("boots")));
		}
		return kitItems;
	}

	public List<KitItem> getInventoryKit(PvPTeam team) {
		String teamId = team.getId();
		List<KitItem> kitItems = new ArrayList<KitItem>();
		ConfigurationSection section = config.getConfigurationSection("kits." + teamId + ".inventory");
		if (section != null) {
			for (String fc : section.getKeys(false)) {
				KitItem kitItem = new KitItem(config.getConfigurationSection("kits." + teamId + ".inventory." + fc));
				kitItems.add(kitItem);
			}
		}
		return kitItems;
	}

	public List<KitItem> getArmorKit(PvPTeam team) {
		String teamId = team.getId();
		List<KitItem> kitItems = new ArrayList<>();
		ConfigurationSection section = config.getConfigurationSection("kits." + teamId + ".armor");
		if (section != null) {
			if (section.isSet("helmet"))
				kitItems.add(new KitItem(section.getConfigurationSection("helmet")));
			if (section.isSet("chestplate"))
				kitItems.add(new KitItem(section.getConfigurationSection("chestplate")));
			if (section.isSet("leggings"))
				kitItems.add(new KitItem(section.getConfigurationSection("leggings")));
			if (section.isSet("boots"))
				kitItems.add(new KitItem(section.getConfigurationSection("boots")));
		}
		return kitItems;
	}

	public FileConfiguration getConfig() {
		return config;
	}

}
