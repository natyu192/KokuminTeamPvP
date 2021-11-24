package me.nucha.teampvp.utils;

import java.util.ArrayList;
import java.util.List;

import me.nucha.teampvp.TeamPvP;

public class ConfigUtil {

	public static List<String> map_rotation;
	public static String scoreboard_title;
	public static List<String> scoreboard_main;

	public static void init(TeamPvP plugin) {
		plugin.getConfig().addDefault("scoreboard.title", "§a§lTeam PvP");
		List<String> sbmain = new ArrayList<>();
		sbmain.add("§fネザースター§aを持って");
		sbmain.add("§a右クリックすると");
		sbmain.add("§eチーム選択メニュー§aが");
		sbmain.add("§a表示されます");
		plugin.getConfig().addDefault("scoreboard.main", sbmain);
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
		map_rotation = plugin.getConfig().getStringList("map-rotation");
		scoreboard_title = plugin.getConfig().getString("scoreboard.title");
		scoreboard_main = plugin.getConfig().getStringList("scoreboard.main");
	}

}
