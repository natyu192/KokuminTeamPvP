package me.nucha.teampvp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
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
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.GameManager;
import me.nucha.teampvp.game.KitManager;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.NavigatorManager;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.TeamManager;
import me.nucha.teampvp.game.objective.GameObjective;
import me.nucha.teampvp.game.objective.GameObjectiveManager;
import me.nucha.teampvp.game.objective.NexusObjective;
import me.nucha.teampvp.game.objective.WoolObjective;
import me.nucha.teampvp.map.config.MapConfig;
import me.nucha.teampvp.map.region.Region;
import me.nucha.teampvp.map.region.TeamRegion;
import me.nucha.teampvp.utils.ScoreboardUtils;
import me.nucha.teampvp.utils.UUIDUtils;

public class PlayerListener implements Listener {

	private TeamPvP plugin;

	public PlayerListener(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		UUIDUtils.putName(p.getUniqueId(), p.getName());
		NavigatorManager.setupPermission(p);
		NavigatorManager.setAllowNavigator(p, true);
		p.setGameMode(GameMode.CREATIVE);
		p.setAllowFlight(true);
		p.setFallDistance(0);
		p.setFireTicks(0);
		p.setLevel(0);
		p.setExp(0f);
		ScoreboardUtils.displayBoard(p);
		TeamManager teamManager = plugin.getTeamManager();
		teamManager.setTeam(p, plugin.getTeamManager().getSpectatorTeam());
		teamManager.getSpectatorTeam().registerOnScoreboard(p);
		event.setJoinMessage("§e" + teamManager.getTeam(p).getColor() + p.getName() + " §ejoined the game.");
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
				if (!plugin.getTeamManager().getSpectatorTeam().contains(all)) {
					// all.showPlayer(p);
					all.hidePlayer(p);
				}
			}
		}
		BukkitRunnable taskOpenInv = new BukkitRunnable() {
			@Override
			public void run() {
				GuiTeamSelector.open(p);
			}
		};
		taskOpenInv.runTaskLater(plugin, 5L);
		BukkitRunnable task = new BukkitRunnable() {
			@Override
			public void run() {
				if (p != null && p.isOnline()) {
					plugin.getMapManager().sendMapInformation(p);
					p.sendMessage("§aチームPvPサーバーへようこそ！");
					p.sendMessage("§e✔§a手元のネザースターをクリックで§bチームを選択できます");
					p.sendMessage("§3✪§a手元の本をクリックすると、マップの説明が表示されます");
					p.sendMessage("");
				}
			};
		};
		task.runTaskLater(plugin, 20L);
		if (p.isOp()) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (all.isOp()) {
					all.sendMessage("§7(Hidden) §r" + event.getJoinMessage());
				}
			}
			event.setJoinMessage(null);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		TeamManager teamManager = plugin.getTeamManager();
		event.setQuitMessage("§e" + teamManager.getTeam(p).getColor() + p.getName() + " §eleft the game.");
		teamManager.quit(p);
		plugin.getStatsManager().removeStats(p);
		if (p.isOp()) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (all.isOp()) {
					all.sendMessage("§7(Hidden) §r" + event.getQuitMessage());
				}
			}
			event.setQuitMessage(null);
		}
		NavigatorManager.removePermission(p);
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
				if (item != null) {
					if (item.getType() == Material.NETHER_STAR) {
						GuiTeamSelector.open(p);
					}
				}
			}
		} else {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block b = event.getClickedBlock();
				if (b.getState() instanceof InventoryHolder) {
					for (Region region : plugin.getRegionManager().getRegions()) {
						if (region instanceof TeamRegion && !region.canBuild() && ((TeamRegion) region).getOwnTeam() != plugin.getTeamManager().getTeam(p)
								&& region.isIn(b)) {
							event.setCancelled(true);
							return;
						}
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
				if (!region.canBuild() && region.isIn(event.getBlock())) {
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
			Block placed = event.getBlockClicked().getRelative(event.getBlockFace());
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
			Block placed = event.getBlockClicked().getRelative(event.getBlockFace());
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
							return;
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
				if (!region.canBuild() && region.isIn(event.getBlock())) {
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
			if (plugin.getTeamManager().getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME)) || p.isDead())
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			TeamManager teamManager = plugin.getTeamManager();
			if (teamManager.getSpectatorTeam().contains(p) || !(MatchState.isState(MatchState.IN_GAME))) {
				event.setCancelled(true);
				if (event.getCause() == DamageCause.VOID) {
					p.teleport(plugin.getKitManager().getSpectatorSpawn());
					return;
				}
			}
			if (MatchState.isState(MatchState.IN_GAME)) {
				if (event.getCause() == DamageCause.VOID) {
					event.setDamage(p.getHealth());
				}
				if (!plugin.getKitManager().allowDamage()) {
					event.setCancelled(true);
					return;
				}
				if (event.getCause() == DamageCause.FALL && !plugin.getKitManager().fallDamage()) {
					event.setCancelled(true);
					return;
				}
				MapConfig mapInfo = plugin.getMapManager().getCurrentMapInfo().getMapConfig();
				if (p.getLocation().distance(mapInfo.getSpawn(teamManager.getTeam(p))) <= mapInfo.getNoDamageRadius()) {
					event.setCancelled(true);
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

	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			all.setPlayerWeather(WeatherType.CLEAR);
		}
	}
}
