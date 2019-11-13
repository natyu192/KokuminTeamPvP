package me.nucha.teampvp.listeners.anni;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.objective.NexusObjective;
import me.nucha.teampvp.utils.ParticleEffect;

public class AnniNexusListener implements Listener {

	private TeamPvP plugin;

	public AnniNexusListener(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler()
	public void onBreakNexus(BlockBreakEvent event) {
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.ANNI) {
			Player p = event.getPlayer();
			Block b = event.getBlock();
			Location l = b.getLocation();
			PvPTeam team = plugin.getTeamManager().getTeam(p);
			for (NexusObjective nexusObjective : plugin.getAnniLocationManager().getNexusObjectives()) {
				if (nexusObjective.getLocation().equals(l)) {
					event.setCancelled(true);
					if (nexusObjective.getHp() <= 0) {
						return;
					}
					if (nexusObjective.getOwnTeam().equals(team)) {
						TeamPvP.sendWarnMessage(p, "そのネクサスは自分のチームのものです");
					} else {
						Material before = b.getType();
						byte beforeData = b.getData();
						b.setType(Material.AIR);
						BukkitRunnable explosionTask = new BukkitRunnable() {
							int i = 6;

							public void run() {
								if (i > 0) {
									i--;
									ParticleEffect.EXPLOSION_LARGE.display(2, 2, 2, 0.1F, 30, l, 32);
								} else {
									cancel();
								}
							}
						};
						BukkitRunnable nexusBreakTask = new BukkitRunnable() {
							public void run() {
								nexusObjective.beAttacked();
								ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0.5F, 50, l.clone().add(0.5d, 0.5d, 0.5d), 32);
								ParticleEffect.LAVA.display(3, 2, 3, 0.5F, 20, l.clone().add(0.5d, 0.5d, 0.5d), 32);
								for (Player all : Bukkit.getOnlinePlayers()) {
									all.playEffect(l, Effect.STEP_SOUND, Material.OBSIDIAN.getId());
								}
								List<Float> floatArray = new ArrayList<>();
								floatArray.add(0.5f);
								floatArray.add(0.6f);
								floatArray.add(0.7f);
								floatArray.add(0.8f);
								floatArray.add(0.9f);
								Collections.shuffle(floatArray);
								l.getWorld().playSound(l, Sound.ANVIL_LAND, 1f, floatArray.get(0));
								for (Player all : Bukkit.getOnlinePlayers()) {
									if (plugin.getTeamManager().getTeam(all).equals(nexusObjective.getOwnTeam())) {
										all.playSound(all.getLocation(), Sound.NOTE_PIANO, 1f, 2f);
									}
								}
								if (nexusObjective.getHp() <= 0) {
									l.getBlock().setType(Material.BEDROCK);
									l.getWorld().playSound(l, Sound.EXPLODE, 256f, 1);
									// PvPTeam winTeam = (nexusObjective.getOwnTeam() == PvPTeam.RED) ? PvPTeam.BLUE : PvPTeam.RED;
									PvPTeam winTeam = plugin.getTeamManager().getTeam(p);
									List<PvPTeam> winners = new ArrayList<>();
									winners.add(winTeam);
									plugin.getGameManager().endGame(winners);
									explosionTask.runTaskTimer(plugin, 5L, 5L);
								} else {
									l.getBlock().setType(before);
									l.getBlock().setData(beforeData);
								}
							}
						};
						nexusBreakTask.runTaskLater(plugin, 5);
					}
					return;
				}
			}
		}
	}
}
