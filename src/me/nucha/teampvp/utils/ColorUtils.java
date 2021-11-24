package me.nucha.teampvp.utils;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

import me.nucha.teampvp.TeamPvP;

public class ColorUtils {

	private static HashMap<ChatColor, DyeColor> chatcolor_to_dyecolor;
	private static HashMap<ChatColor, Color> chatcolor_to_color;

	public static void init(TeamPvP plugin) {
		chatcolor_to_dyecolor = new HashMap<>();
		chatcolor_to_dyecolor.put(ChatColor.WHITE, DyeColor.WHITE);
		chatcolor_to_dyecolor.put(ChatColor.GOLD, DyeColor.ORANGE);
		chatcolor_to_dyecolor.put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA);
		chatcolor_to_dyecolor.put(ChatColor.BLUE, DyeColor.LIGHT_BLUE);
		chatcolor_to_dyecolor.put(ChatColor.YELLOW, DyeColor.YELLOW);
		chatcolor_to_dyecolor.put(ChatColor.GREEN, DyeColor.LIME);
		chatcolor_to_dyecolor.put(ChatColor.RED, DyeColor.PINK);
		chatcolor_to_dyecolor.put(ChatColor.DARK_GRAY, DyeColor.GRAY);
		chatcolor_to_dyecolor.put(ChatColor.GRAY, DyeColor.SILVER);
		chatcolor_to_dyecolor.put(ChatColor.DARK_AQUA, DyeColor.CYAN);
		chatcolor_to_dyecolor.put(ChatColor.DARK_PURPLE, DyeColor.PURPLE);
		chatcolor_to_dyecolor.put(ChatColor.DARK_BLUE, DyeColor.BLUE);
		chatcolor_to_dyecolor.put(ChatColor.DARK_GREEN, DyeColor.GREEN);
		chatcolor_to_dyecolor.put(ChatColor.DARK_RED, DyeColor.RED);
		chatcolor_to_dyecolor.put(ChatColor.BLACK, DyeColor.BLACK);

		chatcolor_to_color = new HashMap<>();
		chatcolor_to_color.put(ChatColor.BLACK, Color.fromRGB(0, 0, 0));
		chatcolor_to_color.put(ChatColor.DARK_BLUE, Color.fromRGB(0, 0, 170));
		chatcolor_to_color.put(ChatColor.DARK_GREEN, Color.fromRGB(0, 170, 0));
		chatcolor_to_color.put(ChatColor.DARK_AQUA, Color.fromRGB(0, 170, 170));
		chatcolor_to_color.put(ChatColor.DARK_RED, Color.fromRGB(170, 0, 0));
		chatcolor_to_color.put(ChatColor.DARK_PURPLE, Color.fromRGB(170, 0, 170));
		chatcolor_to_color.put(ChatColor.GOLD, Color.fromRGB(255, 170, 0));
		chatcolor_to_color.put(ChatColor.GRAY, Color.fromRGB(170, 170, 170));
		chatcolor_to_color.put(ChatColor.DARK_GRAY, Color.fromRGB(85, 85, 85));
		chatcolor_to_color.put(ChatColor.BLUE, Color.fromRGB(85, 85, 255));
		chatcolor_to_color.put(ChatColor.GREEN, Color.fromRGB(85, 255, 85));
		chatcolor_to_color.put(ChatColor.AQUA, Color.fromRGB(85, 255, 255));
		chatcolor_to_color.put(ChatColor.RED, Color.fromRGB(255, 85, 85));
		chatcolor_to_color.put(ChatColor.LIGHT_PURPLE, Color.fromRGB(255, 85, 255));
		chatcolor_to_color.put(ChatColor.YELLOW, Color.fromRGB(255, 255, 85));
		chatcolor_to_color.put(ChatColor.WHITE, Color.fromRGB(255, 255, 255));
	}

	public static DyeColor CHAT_TO_DYE(ChatColor color) {
		return chatcolor_to_dyecolor.get(color);
	}

	public static Color CHAT_TO_COLOR(ChatColor color) {
		return chatcolor_to_color.get(color);
	}

	public static ChatColor COLOR_TO_CHAT(Color color) {
		for (ChatColor chatColor : chatcolor_to_color.keySet()) {
			if (chatcolor_to_color.get(chatColor).equals(color)) {
				return chatColor;
			}
		}
		return ChatColor.WHITE;
	}

	public static ChatColor DYE_TO_CHAT(DyeColor dyeColor) {
		for (ChatColor chatColor : chatcolor_to_dyecolor.keySet()) {
			if (chatcolor_to_dyecolor.get(chatColor).equals(dyeColor)) {
				return chatColor;
			}
		}
		return ChatColor.WHITE;
	}

	public static Color COLORSTR_TO_COLORRGB(String color) {
		if (color.startsWith("#") && color.length() >= 2) {
			String rgbstring = color.substring(1);
			return Color.fromRGB(Integer.valueOf(rgbstring));
		}
		return chatcolor_to_color.get(ChatColor.valueOf(color.toUpperCase()));
	}
}
