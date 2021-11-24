package me.nucha.teampvp.events;

import me.nucha.teampvp.map.region.Region;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionLeaveEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Region region;
	private boolean cancelled;

	public RegionLeaveEvent(Player player, Region region) {
		this.player = player;
		this.region = region;
	}

	public Player getPlayer() {
		return player;
	}

	public Region getRegion() {
		return region;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean flag) {
		this.cancelled = flag;
	}

}
