package me.nucha.teampvp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.GameManager;
import me.nucha.teampvp.game.KitManager;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.TeamManager;
import me.nucha.teampvp.game.objective.GameObjective;
import me.nucha.teampvp.game.objective.GameObjectiveManager;
import me.nucha.teampvp.game.objective.NexusObjective;
import me.nucha.teampvp.game.objective.WoolObjective;
import me.nucha.teampvp.map.region.Region;
import me.nucha.teampvp.utils.ScoreboardUtils;

public class PlayerListener implements Listener {

	private TeamPvP plugin;

	public PlayerListener(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		p.setAllowFlight(true);
		p.setFallDistance(0);
		p.setFireTicks(0);
		p.setLevel(0);
		p.setExp(0f);
		ScoreboardUtils.displayBoard(p);
		plugin.getTeamManager().setTeam(p, plugin.getTeamManager().getSpectatorTeam());
		plugin.getTeamManager().getSpectatorTeam().registerOnScoreboard(p);
		for (PvPTeam team : plugin.getTeamManager().getTeams()) {
			team.registerOnScoreboard(p);
		}
		plugin.getStatsManager().putStats(p);
		if (!MatchState.isState(MatchState.WAITING)) {
			ScoreboardUtils.updateBoard(p);
		}
		if (plugin.getMapInfos().size() > 0) {
			plugin.getGameManager().spawn(p);
			plugin.getKitManager().setSpectatorItem(p);
		}
		if (MatchState.isState(MatchState.IN_GAME)) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (plugin.getTeamManager().getSpectatorTeam().contains(all)) {
					// all.showPlayer(p);
					all.hidePlayer(p);
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		plugin.getTeamManager().quit(p);
		plugin.getStatsManager().removeStats(p);
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			Player t = (Player) event.getTarget();
			if (plugin.getTeamManager().getSpectatorTeam().contains(t)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setCancelled(true);
			ItemStack item = p.getItemInHand();
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
					String name = item.getItemMeta().getDisplayName();
					if (name.equalsIgnoreCase("§aチーム選択")) {
						GuiTeamSelector.open(p);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setCancelled(true);
		} else {
			if (plugin.getGameManager().getTeamGameType() == TeamGameType.ANNI) {
				Block b = event.getBlock();
				Location l = b.getLocation();
				for (NexusObjective nexusObjective : plugin.getAnniLocationManager().getNexusObjectives()) {
					if (nexusObjective.getLocation().equals(l)) {
						return;
					}
				}
			}
			if (!plugin.getKitManager().allowBuild()) {
				TeamPvP.sendWarnMessage(p, "このマップではブロックの破壊ができません");
				event.setCancelled(true);
				return;
			}
			for (Region region : plugin.getRegionManager().getRegions()) {
				if (!region.canBuild() && region.isIn(event.getBlock().getLocation())) {
					TeamPvP.sendWarnMessage(p, "そのエリアのブロックの破壊はできません");
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		Player p = event.getPlayer();
		if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setCancelled(true);
		} else {
			if (!plugin.getKitManager().allowBuild()) {
				TeamPvP.sendWarnMessage(p, "このマップではブロックの破壊ができません");
				event.setCancelled(true);
				return;
			}
			Location placed = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
			for (Region region : plugin.getRegionManager().getRegions()) {
				if (region.isIn(placed)) {
					TeamPvP.sendWarnMessage(p, "そのエリアの水の回収はできません");
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player p = event.getPlayer();
		if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setCancelled(true);
		} else {
			if (!plugin.getKitManager().allowBuild()) {
				TeamPvP.sendWarnMessage(p, "このマップではブロックの破壊ができません");
				event.setCancelled(true);
				return;
			}
			Location placed = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
			for (Region region : plugin.getRegionManager().getRegions()) {
				if (region.isIn(placed)) {
					TeamPvP.sendWarnMessage(p, "そのエリアの水の回収はできません");
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onFoodLevel(FoodLevelChangeEvent event) {
		Player p = (Player) event.getEntity();
		if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setCancelled(true);
		} else {
			if (plugin.getGameManager().getTeamGameType() == TeamGameType.CTW) {
				Block b = event.getBlock();
				if (b.getType() == Material.WOOL) {
					GameObjectiveManager gameObjectiveManager = plugin.getGameObjectiveManager();
					WoolObjective woolObjective = null;
					for (GameObjective objective : gameObjectiveManager.getObjectives()) {
						woolObjective = (WoolObjective) objective;
						if (woolObjective.getLocation().equals(b.getLocation())) {

						}
					}
				}
			}
			if (!plugin.getKitManager().allowBuild()) {
				TeamPvP.sendWarnMessage(p, "このマップではブロックの設置ができません");
				event.setCancelled(true);
				return;
			}
			for (Region region : plugin.getRegionManager().getRegions()) {
				if (!region.canBuild() && region.isIn(event.getBlock().getLocation())) {
					TeamPvP.sendWarnMessage(p, "そのエリアのブロックの設置はできません");
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME)))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (MatchState.isState(MatchState.IN_GAME)) {
				if (event.getCause() == DamageCause.VOID) {
					event.setDamage(40);
					return;
				}
				if (!plugin.getKitManager().allowDamage()) {
					event.setCancelled(true);
					return;
				}
				if (event.getCause() == DamageCause.FALL && !plugin.getKitManager().fallDamage()) {
					event.setCancelled(true);
					return;
				}
			}
			if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
				event.setCancelled(true);
				if (event.getCause() == DamageCause.VOID) {
					p.teleport(plugin.getKitManager().getSpectatorSpawn());
					return;
				}
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		KitManager kitManager = plugin.getKitManager();
		TeamManager teamManager = plugin.getTeamManager();
		PvPTeam team = teamManager.getTeam(p);
		if (team == teamManager.getSpectatorTeam() || !(MatchState.isState(MatchState.IN_GAME))) {
			event.setRespawnLocation(kitManager.getSpectatorSpawn());
		} else {
			event.setRespawnLocation(kitManager.getSpawn(team));
		}
		GameManager gameManager = plugin.getGameManager();
		gameManager.giveKit(p);
	}
}
