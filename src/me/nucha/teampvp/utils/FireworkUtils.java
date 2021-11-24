package me.nucha.teampvp.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;

public class FireworkUtils {

	public static Firework launch(Player p, int power) {
		TeamPvP plugin = TeamPvP.getInstance();
		PvPTeam team = plugin.getTeamManager().getTeam(p);
		return launch(p.getLocation(), team, power);
	}

	public static Firework launch(Location l, PvPTeam team, int power) {
		Color color = ColorUtils.CHAT_TO_COLOR(team.getColor());
		Firework fw = (Firework) l.getWorld().spawn(l, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(Type.BALL).withColor(color).withFade(Color.WHITE).build());
		fm.setPower(power);
		fw.setFireworkMeta(fm);
		return fw;
	}

}
