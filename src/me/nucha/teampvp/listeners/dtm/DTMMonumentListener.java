package me.nucha.teampvp.listeners.dtm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.NumberConversions;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.objective.GameObjectiveManager;
import me.nucha.teampvp.game.objective.GameObjectiveState;
import me.nucha.teampvp.game.objective.MonumentObjective;
import me.nucha.teampvp.game.stats.StatsCategory;
import me.nucha.teampvp.game.stats.StatsManager;
import me.nucha.teampvp.map.region.MonumentRegion;
import me.nucha.teampvp.utils.FireworkUtils;

public class DTMMonumentListener implements Listener {

	private TeamPvP plugin;
	private HashMap<Player, MonumentRegion> nobroadcast;

	public DTMMonumentListener(TeamPvP plugin) {
		this.plugin = plugin;
		nobroadcast = new HashMap<>();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.DTM) {
			Block b = event.getBlock();
			PvPTeam team = plugin.getTeamManager().getTeam(p);
			GameObjectiveManager gameObjectiveManager = plugin.getGameObjectiveManager();
			for (MonumentRegion region : plugin.getDtmMonumentManager().getMonumentRegions()) {
				if (region.destroyed()) {
					continue;
				}
				if (!region.isBroken(b.getLocation()) && region.isInBlock(b.getLocation())) {
					// if (region.getOwnTeam() != team) {
					if (region.getOwnTeam() == team) {
						region.addBroken(b.getLocation());
						b.setType(Material.AIR);
						int remains = region.remains();
						StatsManager statsManager = plugin.getStatsManager();
						MonumentObjective obj = (MonumentObjective) gameObjectiveManager.getObjective(region.getName(), team);
						if (remains > 0) {
							if (!nobroadcast.containsKey(p) || !nobroadcast.get(p).getName().equalsIgnoreCase(region.getName())) {
								Bukkit.broadcastMessage(team.getColor().toString() + "[" + team.getDisplayName()
										+ team.getColor().toString() + "] " + p.getDisplayName() + " §rがモニュメント "
										+ region.getOwnTeam().getColor().toString() + region.getName() + " §rを破壊しています");
								nobroadcast.put(p, region);
								for (Player all : Bukkit.getOnlinePlayers()) {
									all.playSound(b.getLocation(), Sound.FIREWORK_TWINKLE, 1f, 1f);
								}
							}
						}
						obj.breakPieceOfMonument(p);
						if (region.destroyed()) {
							obj.setState(GameObjectiveState.COPLETED);
							String percentages = "";
							boolean first = true;
							for (String name : obj.getBreakCount().keySet()) {
								double percentage = obj.getPercentageOfContribute(name);
								ChatColor color = plugin.getTeamManager().getTeam(p).getColor();
								if (first) {
									percentages = color + name + "§8(§e" + percentage + "%§8)";
									first = false;
								} else {
									percentages += ", " + color + name + "§8(§e" + percentage + "%§8)";
								}
								int rewardCoins = NumberConversions.floor(300 * (percentage / 100));
								if (TeamPvP.pl_kokuminserver) {
									Player whoBroke = Bukkit.getPlayer(name);
									if (whoBroke != null) {
										TeamPvP.addCoin(whoBroke, rewardCoins, "モニュメントの" + percentage + "%を破壊");
									}
								}
							}
							Bukkit.broadcastMessage(region.getOwnTeam().getDisplayName() + " §eのモニュメント "
									+ region.getOwnTeam().getColor().toString() + region.getName() + " §eが破壊されました: " + percentages);
							Location loc = b.getLocation();
							FireworkUtils.launch(loc.clone().add(0, 0, 1), team, 2);
							FireworkUtils.launch(loc.clone().add(0, 0, -1), team, 2);
							FireworkUtils.launch(loc.clone().add(1, 0, 0), team, 2);
							FireworkUtils.launch(loc.clone().add(-1, 0, 0), team, 2);
							statsManager.getStatsInfo(p).add(StatsCategory.MONUMENTS_BROKEN, 1);
						} else {
							obj.setState(GameObjectiveState.SEMI_COMPLETED);
						}
						boolean endGame = true;
						for (MonumentRegion region2 : plugin.getDtmMonumentManager().getMonumentRegions()) {
							if (region2.getOwnTeam() == region.getOwnTeam() && !region2.destroyed()) {
								endGame = false;
							}
						}
						if (endGame) {
							List<PvPTeam> winners = new ArrayList<>();
							winners.add(team);
							plugin.getGameManager().endGame(winners);
						}

					} else {
						TeamPvP.sendWarnMessage(p, "それはあなたのチームのモニュメントです");
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (nobroadcast.containsKey(p)) {
			nobroadcast.remove(p);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (nobroadcast.containsKey(p)) {
			nobroadcast.remove(p);
		}
	}

}
