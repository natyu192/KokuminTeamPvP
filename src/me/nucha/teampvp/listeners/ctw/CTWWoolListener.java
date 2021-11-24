package me.nucha.teampvp.listeners.ctw;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.objective.GameObjective;
import me.nucha.teampvp.game.objective.GameObjectiveManager;
import me.nucha.teampvp.game.objective.GameObjectiveState;
import me.nucha.teampvp.game.objective.WoolObjective;
import me.nucha.teampvp.game.stats.StatsCategory;
import me.nucha.teampvp.game.stats.StatsManager;
import me.nucha.teampvp.utils.FireworkUtils;

public class CTWWoolListener implements Listener {

	private TeamPvP plugin;

	public CTWWoolListener(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (event.isCancelled())
			return;
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.CTW) {
			ItemStack item = event.getItem().getItemStack();
			if (item.getType() == Material.WOOL) {
				Player p = event.getPlayer();
				DyeColor color = DyeColor.getByWoolData(item.getData().getData());
				PvPTeam team = plugin.getTeamManager().getTeam(p);
				GameObjectiveManager gameObjectiveManager = plugin.getGameObjectiveManager();
				for (GameObjective objective : gameObjectiveManager.getObjectives()) {
					if (objective.getState() == GameObjectiveState.COPLETED) {
						continue;
					}
					WoolObjective woolObjective = (WoolObjective) objective;
					if (woolObjective.getColor().equals(color) && woolObjective.getOwnTeam().equals(team)) {
						woolObjective.setState(GameObjectiveState.SEMI_COMPLETED);
						if (!woolObjective.hasPicked(p)) {
							Bukkit.broadcastMessage(p.getDisplayName() + " §7が " + woolObjective.getChatColor()
									+ woolObjective.getWoolName() + " §7を取得しました");
							if (TeamPvP.pl_kokuminserver) {
								if (!woolObjective.hasPickedAtLeastOnce(p)) {
									TeamPvP.addCoin(p, 50, "羊毛を取得");
								}
							}
							woolObjective.picked(p);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onClickInv(InventoryClickEvent event) {
		if (event.isCancelled())
			return;
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.CTW) {
			ItemStack item = event.getCurrentItem();
			if (item != null && item.getType() == Material.WOOL) {
				Player p = (Player) event.getWhoClicked();
				DyeColor color = DyeColor.getByWoolData(item.getData().getData());
				PvPTeam team = plugin.getTeamManager().getTeam(p);
				GameObjectiveManager gameObjectiveManager = plugin.getGameObjectiveManager();
				for (GameObjective objective : gameObjectiveManager.getObjectives()) {
					if (objective.getState() == GameObjectiveState.COPLETED) {
						continue;
					}
					WoolObjective woolObjective = (WoolObjective) objective;
					if (woolObjective.getColor().equals(color) && woolObjective.getOwnTeam().equals(team)) {
						woolObjective.setState(GameObjectiveState.SEMI_COMPLETED);
						if (!woolObjective.hasPicked(p)) {
							Bukkit.broadcastMessage(p.getDisplayName() + " §7が " + woolObjective.getChatColor()
									+ woolObjective.getWoolName() + " §7を取得しました");
							if (TeamPvP.pl_kokuminserver) {
								if (!woolObjective.hasPickedAtLeastOnce(p)) {
									TeamPvP.addCoin(p, 50, "羊毛を取得");
								}
							}
							woolObjective.picked(p);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.CTW) {
			Player p = event.getEntity();
			GameObjectiveManager gameObjectiveManager = plugin.getGameObjectiveManager();
			for (GameObjective objective : gameObjectiveManager.getObjectives()) {
				if (objective.getState() != GameObjectiveState.SEMI_COMPLETED) {
					continue;
				}
				WoolObjective woolObjective = (WoolObjective) objective;
				woolObjective.lost(p);
				if (woolObjective.getPickers().size() <= 0) {
					woolObjective.setState(GameObjectiveState.IN_COPLETE);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent event) {
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.CTW) {
			Block b = event.getBlock();
			if (b.getType() == Material.WOOL) {
				Player p = event.getPlayer();
				DyeColor color = DyeColor.getByWoolData(b.getData());
				PvPTeam team = plugin.getTeamManager().getTeam(p);
				GameObjectiveManager gameObjectiveManager = plugin.getGameObjectiveManager();
				WoolObjective woolObjective = null;
				for (GameObjective objective : gameObjectiveManager.getObjectives()) {
					if (objective.getState() == GameObjectiveState.COPLETED) {
						continue;
					}
					woolObjective = (WoolObjective) objective;
					if (woolObjective.getOwnTeam().equals(team) && woolObjective.getLocation().equals(b.getLocation())) {
						if (woolObjective.getColor().equals(color)) {
							event.setCancelled(false);
							StatsManager statsManager = plugin.getStatsManager();
							statsManager.getStatsInfo(p).add(StatsCategory.WOOLS_PLACED, 1);
							woolObjective.place();
							woolObjective.setState(GameObjectiveState.COPLETED);
							Bukkit.broadcastMessage(p.getDisplayName() + " §7が " + woolObjective.getChatColor()
									+ woolObjective.getWoolName() + " §7を設置しました");
							if (TeamPvP.pl_kokuminserver) {
								TeamPvP.addCoin(p, 150, "羊毛を設置");
							}
							FireworkUtils.launch(b.getLocation().add(0.5, 0, 0.5), team, 1);
						} else {
							event.setCancelled(true);
							TeamPvP.sendWarnMessage(p, "そこは " + woolObjective.getChatColor() + woolObjective.getWoolName() + " §cを置く場所です");
							return;
						}
					}
				}

				boolean endGame = true;
				for (WoolObjective wo : plugin.getCtwWoolManager().getWoolObjectives()) {
					if (wo.getOwnTeam() == team && !wo.isPlaced()) {
						endGame = false;
					}
				}
				if (endGame) {
					List<PvPTeam> winners = new ArrayList<>();
					winners.add(team);
					plugin.getGameManager().endGame(winners);
				}

			}
		}
	}

}
