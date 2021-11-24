package me.nucha.teampvp.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	public MatchEndEvent() {

	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}
