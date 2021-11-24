package me.nucha.teampvp.map;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import com.google.common.io.Files;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.KitManager;
import me.nucha.teampvp.game.chunk.NullChunkGenerator;
import me.nucha.teampvp.map.config.MapConfig;

public class MapManager {

	private TeamPvP plugin;
	private String currentMap;
	private String nextMap;
	private String theWorldNameThatUsing;

	public MapManager(TeamPvP plugin) {
		this.plugin = plugin;
		this.theWorldNameThatUsing = "match1";
	}

	public void loadMap(String name) {
		if (plugin.getMapInfos().size() > 0) {
			for (MapInfo mapInfo : plugin.getMapInfos()) {
				if (mapInfo.getName().equalsIgnoreCase(name)) {
					loadMap(mapInfo);
					return;
				}
			}
			TeamPvP.sendConsoleMessage("§cMap " + name + " が見つかりません");
		}
	}

	public void loadMap(MapInfo mapInfo) {
		if (plugin.getMapInfos().size() > 0) {
			TeamPvP.sendConsoleMessage("§bMap " + mapInfo.getName() + " を読み込みます...");
			if (theWorldNameThatUsing.equalsIgnoreCase("match1")) {
				theWorldNameThatUsing = "match2";
			} else {
				theWorldNameThatUsing = "match1";
			}
			try {
				Bukkit.unloadWorld(theWorldNameThatUsing, false);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				TeamPvP.sendConsoleMessage("§cWorld " + getCurrentMap() + "(" + theWorldNameThatUsing + ") をアンロード中にエラーが発生しました");
			}
			/*if (Bukkit.getWorld(mapInfo.getName()) != null) {
				TeamPvP.sendConsoleMessage("§eMap " + mapInfo.getName() + " が存在するため、アンロードします");
				Bukkit.unloadWorld(mapInfo.getName(), false);
			}*/
			TeamPvP.sendConsoleMessage("§9コピー中...");
			File worldFile = new File(Bukkit.getWorldContainer(), theWorldNameThatUsing);
			if (!worldFile.exists()) {
				worldFile.mkdir();
			}
			File datFile = new File(worldFile, "level.dat");
			File ymlFile = new File(worldFile, "config.yml");
			File dest_region = new File(worldFile, "region");
			// File dest_data = new File(worldFile, "data");
			/*datFile.delete();
			ymlFile.delete();
			dest_region.delete();*/
			// dest_data.delete();
			File dest_dat = new File(mapInfo.getWorldFile(), "level.dat");
			File dest_yml = new File(mapInfo.getWorldFile().getParentFile(), "config.yml");
			try {
				if (!dest_dat.exists())
					dest_dat.createNewFile();
				if (!dest_yml.exists())
					dest_yml.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Files.copy(new File(mapInfo.getWorldFile() + "/level.dat"), datFile);
				Files.copy(new File(mapInfo.getWorldFile().getParentFile() + "/config.yml"), ymlFile);
				FileUtils.copyDirectory(new File(mapInfo.getWorldFile() + "/region"), dest_region);
				// FileUtils.copyDirectory(new File(mapInfo.getWorldFile() + "/data"), dest_data);
			} catch (IOException e) {
				TeamPvP.sendConsoleMessage("§cMap " + mapInfo.getName() + " をコピー中にエラーが発生しました");
				e.printStackTrace();
			} /* else {
				try {
				TeamPvP.sendConsoleMessage("§9コピー中...");
				File worldFile = mapInfo.getWorldFile();
				File worldContainer = Bukkit.getServer().getWorldContainer();
				File destFile = new File(worldContainer, mapInfo.getName());
				File dest_level_dat = new File(destFile + "/level.dat");
				File dest_config_yml = new File(destFile + "/config.yml");
				File dest_data = new File(destFile, "data");
				if (!destFile.exists())
					destFile.mkdir();
				if (dest_config_yml.exists())
					dest_config_yml.delete();
				if (dest_data.exists()) {
					dest_data.delete();
				}
				Files.copy(new File(worldFile + "/level.dat"), dest_level_dat);
				Files.copy(new File(worldFile.getParentFile() + "/config.yml"), dest_config_yml);
				FileUtils.copyDirectory(new File(worldFile, "data"), new File(destFile, "data"));
				} catch (IOException e) {
				TeamPvP.sendConsoleMessage("§cMap " + mapInfo.getName() + " をコピー中にエラーが発生しました");
				e.printStackTrace();
				return;
				}
				}*/
			TeamPvP.sendConsoleMessage("§9読み込み中...");
			mapInfo.setConfigFile(ymlFile);
			WorldCreator wc = new WorldCreator(theWorldNameThatUsing);
			wc.generator(new NullChunkGenerator());
			World world = Bukkit.getServer().createWorld(wc);
			world.setAutoSave(false);
			world.setKeepSpawnInMemory(false);
			world.setGameRuleValue("doMobSpawning", "false");
			TeamPvP.sendConsoleMessage("§bMap " + mapInfo.getName() + " の読み込みが完了しました");
			nextMap = null;
		}
	}

	public MapInfo getMapInfoByName(String name) {
		for (MapInfo mapInfo : plugin.getMapInfos()) {
			if (mapInfo.getName().equalsIgnoreCase(name)) {
				return mapInfo;
			}
		}
		return null;
	}

	public MapInfo setCurrentMap(String name) {
		MapInfo mapInfo = getMapInfoByName(name);
		if (mapInfo != null) {
			loadMap(mapInfo);
			this.currentMap = name;
			MapConfig mc = new MapConfig(mapInfo.getConfig());
			plugin.setKitManager(new KitManager(plugin, mc));
			plugin.getGameManager().setTeamGameType(mc.getTeamGameType());
			for (Player all : Bukkit.getOnlinePlayers()) {
				plugin.getGameManager().spawn(all);
			}
		}
		return mapInfo;
	}

	public String getCurrentMap() {
		return currentMap;
	}

	public String getNextMap() {
		if (nextMap != null) {
			return nextMap;
		}
		int nextIndex = plugin.getMapInfos().indexOf(getMapInfoByName(currentMap)) + 1;
		try {
			return plugin.getMapInfos().get(nextIndex).getName();
		} catch (IndexOutOfBoundsException e) {
			TeamPvP.sendConsoleMessage("§eローテーションが最初に戻りました");
			return plugin.getMapInfos().get(0).getName();
		}
	}

	public void setNextMap(String nextMap) {
		this.nextMap = nextMap;
	}

	public MapInfo getCurrentMapInfo() {
		return getMapInfoByName(currentMap);
	}

	public void sendMapInformation(Player p) {
		p.sendMessage("§5--------------------------------------------------");
		p.sendMessage("§2Map: §a" + getCurrentMapInfo().getName());
		String authors = getCurrentMapInfo().getAuthors();
		if (!authors.isEmpty()) {
			p.sendMessage("§2Author(s): §a" + authors);
		}
		List<String> description = getCurrentMapInfo().getMapConfig().getDescription();
		if (description != null && !description.isEmpty()) {
			p.sendMessage("");
			for (String descriptionLine : description) {
				descriptionLine = ChatColor.translateAlternateColorCodes('&', descriptionLine);
				p.sendMessage("§e" + descriptionLine);
			}
		}
		p.sendMessage("§5--------------------------------------------------");
	}

	public void sendMapDescription(Player p) {
		List<String> description = getCurrentMapInfo().getMapConfig().getDescription();
		if (description != null) {
			p.sendMessage("");
			for (String desc : description) {
				desc = ChatColor.translateAlternateColorCodes('&', desc);
				p.sendMessage("§b" + desc);
			}
			p.sendMessage("");
		}
	}

	public String getTheWorldNameThatUsing() {
		return theWorldNameThatUsing;
	}

}
