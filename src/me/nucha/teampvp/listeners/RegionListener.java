package me.nucha.teampvp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.events.RegionEnterEvent;
import me.nucha.teampvp.events.RegionLeaveEvent;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.map.region.Region;
import me.nucha.teampvp.map.region.RegionManager;
import me.nucha.teampvp.map.region.TeamRegion;

public class RegionListener implements Listener {

	private TeamPvP plugin;
	private RegionManager regionManager;

	public RegionListener(TeamPvP plugin) {
		this.plugin = plugin;
		this.regionManager = plugin.getRegionManager();
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();
		Location toBlock = to.getBlock().getLocation();
		Location fromBlock = from.getBlock().getLocation();
		if (fromBlock.getY() <= 0) {
			return;
		}
		if (toBlock.getX() != fromBlock.getX() || toBlock.getY() != fromBlock.getY() || toBlock.getZ() != fromBlock.getZ()) {
			if (!plugin.getTeamManager().getSpectatorTeam().contains(p) && (MatchState.isState(MatchState.IN_GAME))) {
				for (Region r : regionManager.getRegions()) {
					if (!r.containPlayers.contains(p)) {
						if (r.isIn(to)) {
							RegionEnterEvent regionEnterEvent = new RegionEnterEvent(p, r);
							Bukkit.getServer().getPluginManager().callEvent(regionEnterEvent);
							if (regionEnterEvent.isCancelled()) {
								p.teleport(from);
								int id = Material.REDSTONE_WIRE.getId();
								for (Player all : Bukkit.getOnlinePlayers()) {
									all.playEffect(toBlock, Effect.STEP_SOUND, id);
								}
							} else {
								r.containPlayers.add(p);
							}
						}
					}
					if (r.containPlayers.contains(p)) {
						if (!r.isIn(to)) {
							RegionLeaveEvent regionLeaveEvent = new RegionLeaveEvent(p, r);
							Bukkit.getServer().getPluginManager().callEvent(regionLeaveEvent);
							if (regionLeaveEvent.isCancelled()) {
								p.teleport(from);
								int id = Material.REDSTONE_WIRE.getId();
								for (Player all : Bukkit.getOnlinePlayers()) {
									all.playEffect(toBlock, Effect.STEP_SOUND, id);
								}
							} else {
								r.containPlayers.remove(p);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.ENDER_PEARL) {
			Player p = event.getPlayer();
			if (!plugin.getTeamManager().getSpectatorTeam().contains(p) && (MatchState.isState(MatchState.IN_GAME))) {
				for (Region r : regionManager.getRegions()) {
					if (!r.containPlayers.contains(p)) {
						if (r.isIn(event.getTo())) {
							RegionEnterEvent regionEnterEvent = new RegionEnterEvent(p, r);
							Bukkit.getServer().getPluginManager().callEvent(regionEnterEvent);
							if (regionEnterEvent.isCancelled())
								event.setCancelled(true);
							else
								r.containPlayers.add(p);
						}
					}
					if (r.containPlayers.contains(p)) {
						if (!r.isIn(event.getTo())) {
							RegionLeaveEvent regionLeaveEvent = new RegionLeaveEvent(p, r);
							Bukkit.getServer().getPluginManager().callEvent(regionLeaveEvent);
							if (regionLeaveEvent.isCancelled())
								event.setCancelled(true);
							else
								r.containPlayers.remove(p);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEnter(RegionEnterEvent event) {
		Player p = event.getPlayer();
		if (!(event.getRegion() instanceof TeamRegion))
			return;
		TeamRegion r = (TeamRegion) event.getRegion();
		if (r.canEnter()) {
			return;
		}
		PvPTeam team = plugin.getTeamManager().getTeam(p);
		if (team != r.getOwnTeam()) {
			TeamPvP.sendWarnMessage(p, r.getDenyMessage());
			event.setCancelled(true);
		}
	}

}
