package me.nucha.teampvp.utils;

import java.math.BigDecimal;

import org.bukkit.util.NumberConversions;

public class MathUtils {

	public static double percentage(int max, double value, int scale) {
		if (value == 0.0) {
			return 0.0;
		}
		double percentage = value / max * 100;
		if (scale > 0) {
			BigDecimal bigDecimal = new BigDecimal(percentage);
			return bigDecimal.setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
		} else {
			return NumberConversions.floor(percentage);
		}
	}

	public static double percentage(int max, double value) {
		return percentage(max, value, 0);
	}

}
