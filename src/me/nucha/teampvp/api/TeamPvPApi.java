package me.nucha.teampvp.api;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.listeners.ChatListener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TeamPvPApi {

	private static TeamPvP plugin;

	public static void plugin(TeamPvP plugin) {
		TeamPvPApi.plugin = plugin;
	}

	public static TeamPvP getInstance() {
		return plugin;
	}

	public static void chatEvent(AsyncPlayerChatEvent event) {
		ChatListener.onChat(event);
	}

}
