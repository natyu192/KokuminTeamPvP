package me.nucha.teampvp.game;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import me.nucha.teampvp.TeamPvP;

public class NavigatorManager {

	private static TeamPvP plugin;
	private static HashMap<Player, PermissionAttachment> permissions;

	public static void init(TeamPvP plugin) {
		NavigatorManager.plugin = plugin;
		permissions = new HashMap<>();
	}

	public static void setupPermission(Player p) {
		permissions.put(p, p.addAttachment(plugin));
	}

	public static void removePermission(Player p) {
		p.removeAttachment(permissions.get(p));
		permissions.remove(p);
	}

	public static void setAllowNavigator(Player p, boolean allow) {
		permissions.get(p).setPermission("worldedit.navigation.jumpto.tool", allow);
		permissions.get(p).setPermission("worldedit.navigation.thru.tool", allow);
	}

}
