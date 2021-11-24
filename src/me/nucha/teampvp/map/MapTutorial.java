package me.nucha.teampvp.map;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MapTutorial {

	private Location location;
	private List<String> messages;

	public MapTutorial(Location location, List<String> messages) {
		this.location = location;
		this.messages = messages;
	}

	public Location getLocation() {
		return location;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void to(Player p) {
		if (location != null) {
			p.teleport(location);
			p.setFlying(true);
		}
		if (messages != null && !messages.isEmpty()) {
			p.sendMessage(" ");
			for (String message : messages) {
				message = ChatColor.translateAlternateColorCodes('&', message);
				p.sendMessage(message);
			}
			p.sendMessage(" ");
		}
	}

}
