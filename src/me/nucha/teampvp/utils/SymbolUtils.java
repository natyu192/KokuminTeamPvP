package me.nucha.teampvp.utils;


public class SymbolUtils {

	public static String warn() {
		return "⚠";
	}

	public static String x() {
		return "✕";
	}

	public static String check() {
		return "✔";
	}

	public static String wool_placed() {
		return "█";
	}

	public static String wool_pickedup() {
		return "▒";
	}

	public static String wool_unpicked() {
		return "⬜";
	}

	public static String star(int level) {
		switch (level) {
		case 2:
			return "✶";
		case 3:
			return "✷";
		case 4:
			return "✸";
		case 5:
			return "✹";
		case 6:
			return "✺";
		default:
			return "✴";
		}
	}

}
