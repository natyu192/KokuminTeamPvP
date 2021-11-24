
package me.nucha.teampvp.game;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Team;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.events.MatchEndEvent;
import me.nucha.teampvp.events.MatchStartEvent;
import me.nucha.teampvp.game.objective.GameObjectiveManager;
import me.nucha.teampvp.game.stats.StatsCategory;
import me.nucha.teampvp.game.stats.StatsManager;
import me.nucha.teampvp.listeners.GuiTeamSelector;
import me.nucha.teampvp.listeners.KillListener;
import me.nucha.teampvp.listeners.anni.AnniLocationManager;
import me.nucha.teampvp.listeners.anni.AnniNexusListener;
import me.nucha.teampvp.listeners.anni.AnniResourceListener;
import me.nucha.teampvp.listeners.anni.EnderFurnaceListener;
import me.nucha.teampvp.listeners.ctw.CTWWoolListener;
import me.nucha.teampvp.listeners.ctw.CTWWoolManager;
import me.nucha.teampvp.listeners.dtm.DTMMonumentListener;
import me.nucha.teampvp.listeners.dtm.DTMMonumentManager;
import me.nucha.teampvp.listeners.tdm.TDMCombatListener;
import me.nucha.teampvp.listeners.tdm.TDMScoreManager;
import me.nucha.teampvp.map.MapManager;
import me.nucha.teampvp.map.config.TDMConfig;
import me.nucha.teampvp.map.item.KitItem;
import me.nucha.teampvp.map.region.Region;
import me.nucha.teampvp.utils.FireworkUtils;
import me.nucha.teampvp.utils.ScoreboardUtils;
import me.nucha.teampvp.utils.TitleUtil;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class GameManager {

	private TeamPvP plugin;
	private TeamGameType teamGameType;
	private int startingTaskId;
	private int cyclingTaskId;
	private int matchTaskId;
	private int fireworkTaskId;
	private int duration;
	private MatchState stateBeforeCycling;

	private TDMCombatListener tdmCombatListener;

	private DTMMonumentListener dtmMonumentListener;

	private CTWWoolListener ctwWoolListener;

	private AnniNexusListener anniNexusListener;
	private EnderFurnaceListener enderFurnaceListener;
	private AnniResourceListener anniResourceListener;

	public GameManager(TeamPvP plugin) {
		this.plugin = plugin;
	}

	public void startGame() {
		startGame(15);
	}

	public void startGame(int count) {
		if (!MatchState.isState(MatchState.WAITING))
			return;
		MatchState.setState(MatchState.COUNTDOWN);
		startingTaskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			int i = count;
			int maxcount = count;

			@Override
			public void run() {
				if (i == 0) {
					Bukkit.broadcastMessage("§a=========================");
					Bukkit.broadcastMessage("§aゲームが開始しました！");
					Bukkit.broadcastMessage("§a=========================");
					plugin.setGameObjectiveManager(new GameObjectiveManager());
					for (Player all : plugin.getTeamManager().getGamePlayers()) {
						spawn(all);
						plugin.getMapManager().sendMapDescription(all);
						NavigatorManager.setAllowNavigator(all, false);
					}
					for (Player all : Bukkit.getOnlinePlayers()) {
						all.playSound(all.getLocation(), Sound.NOTE_PLING, 1f, 2f);
					}
					for (Region r : plugin.getKitManager().getRegions()) {
						plugin.getRegionManager().registerRegion(r);
					}
					// TODO Games Initialization
					if (getTeamGameType() == TeamGameType.DTM) {
						plugin.getDtmMonumentManager().load();
					}
					if (getTeamGameType() == TeamGameType.CTW) {
						plugin.getCtwWoolManager().load();
					}
					if (getTeamGameType() == TeamGameType.ANNI) {
						plugin.getAnniLocationManager().load();
					}
					TeamManager teamManager = plugin.getTeamManager();
					for (Player player : teamManager.getGamePlayers()) {
						for (Player spectator : teamManager.getSpectators()) {
							if (spectator != null) {
								player.hidePlayer(spectator);
							}
						}
					}
					MatchState.setState(MatchState.IN_GAME);
					startGameTimer();
					MatchStartEvent event = new MatchStartEvent();
					Bukkit.getServer().getPluginManager().callEvent(event);
					i--;
				}
				if (i > 0) {
					if (i == maxcount || i % 10 == 0 || i == 15 || i == 10 || (i > 0 && i <= 5)) {
						Bukkit.broadcastMessage("§aゲーム開始まで後§c" + i + "§a秒...");
						if (i > 0 && i <= 5) {
							for (Player all : Bukkit.getOnlinePlayers()) {
								all.playSound(all.getLocation(), Sound.NOTE_PLING, 1f, 1f);
							}
						}
					}
					i--;
				}
			}
		}, 0L, 20L).getTaskId();
	}

	public void endGame(List<PvPTeam> winTeam) {
		if (!MatchState.isState(MatchState.IN_GAME))
			return;
		MatchState.setState(MatchState.ENDING);
		Bukkit.getScheduler().cancelTask(matchTaskId);
		TeamManager teamManager = plugin.getTeamManager();
		for (Player player : teamManager.getGamePlayers()) {
			for (Player spectator : teamManager.getSpectators()) {
				if (spectator != null) {
					player.showPlayer(spectator);
				}
			}
		}
		String winners = null;
		if (!(winTeam == null || winTeam.size() == 0)) {
			StringBuilder winnersBuilder = new StringBuilder(winTeam.get(0).getDisplayName());
			boolean flag = true;
			for (PvPTeam team : winTeam) {
				if (flag) {
					flag = false;
					continue;
				}
				winnersBuilder.append(", " + team.getDisplayName());
			}
			winners = new String(winnersBuilder);
		}
		Bukkit.broadcastMessage("§a=========================");
		Bukkit.broadcastMessage("§a  §6ゲームが終了しました！");
		if (winTeam == null || winTeam.size() == 0) {
			Bukkit.broadcastMessage("§a  §e引き分けです...");
		} else {
			Bukkit.broadcastMessage("§a  " + winners + "§aの勝利！");
		}
		Bukkit.broadcastMessage("§a=========================");
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (all.isDead()) {
				KillListener.respawn(all);
			}
			NavigatorManager.setAllowNavigator(all, true);
			all.setGameMode(GameMode.CREATIVE);
			all.getInventory().clear();
			all.setFoodLevel(20);
			all.setHealth(20);
			if (winTeam != null) {
				TitleUtil.sendTitle(all, winners, EnumTitleAction.TITLE, 20, 60, 20);
				TitleUtil.sendTitle(all, "§aが勝ちました！", EnumTitleAction.SUBTITLE, 20, 60, 20);
			} else {
				TitleUtil.sendTitle(all, "§7引き分け", EnumTitleAction.TITLE, 20, 60, 20);
			}
		}
		StatsManager statsManager = plugin.getStatsManager();
		if (winTeam != null) {
			for (PvPTeam team : winTeam) {
				for (Player p : team.getTeamMembers()) {
					if (p != null) {
						p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1f, 1f);
					}
					TitleUtil.sendTitle(p, "§bあなたのチームは勝ちました！", EnumTitleAction.SUBTITLE);
					statsManager.getStatsInfo(p).add(StatsCategory.WINS, 1);
				}
			}
			for (Player p : teamManager.getGamePlayers()) {
				if (!winTeam.contains(teamManager.getTeam(p))) {
					if (p != null) {
						p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1f, 1f);
					}
					TitleUtil.sendTitle(p, "§7あなたのチームは負けました...", EnumTitleAction.SUBTITLE);
					statsManager.getStatsInfo(p).add(StatsCategory.LOSES, 1);
				}
			}
			fireworkTaskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				int i = 10;
				Random rnd = new Random();

				@Override
				public void run() {
					if (i > 0) {
						for (PvPTeam team : winTeam) {
							for (Player p : team.getTeamMembers()) {
								if (p != null) {
									FireworkUtils.launch(p, rnd.nextInt(3) + 1);
								}
							}
						}
						i--;
					}
				}
			}, 0L, 20L).getTaskId();
		}

		plugin.getRegionManager().clearRegions();
		startCycling();
		plugin.getStatsManager().save();
		MatchEndEvent event = new MatchEndEvent();
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	public void startGameTimer() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			Team team = ScoreboardUtils.getOrCreateTeam(all, "Duration: §b");
			team.setSuffix("00:00");
		}
		Bukkit.getScheduler().cancelTask(startingTaskId);
		matchTaskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			int i = 0;

			@Override
			public void run() {
				i++;
				duration = i;
				for (Player all : Bukkit.getOnlinePlayers()) {
					Team team = ScoreboardUtils.getOrCreateTeam(all, "Duration: §b");
					team.setSuffix(ScoreboardUtils.toMinAndSec(i));
				}
				if (getTeamGameType() == TeamGameType.TDM && i >= ((TDMConfig) plugin.getMapManager().getCurrentMapInfo().getMapConfig()).getTime()) {
					TDMScoreManager tdmScoreManager = plugin.getTdmScoreManager();
					endGame(tdmScoreManager.getTops());
				}
				if (i == 60 * 30) {
					endGame(null);
				}
			}
		}, 20L, 20L).getTaskId();
	}

	public void startCycling(int count) {
		startCycling(null, count);
	}

	public void startCycling() {
		startCycling(null, 15);
	}

	public void startCycling(String next, int count) {
		stateBeforeCycling = MatchState.getState();
		MatchState.setState(MatchState.CYCLING);
		cyclingTaskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			int i = count;
			int maxcount = count;

			@Override
			public void run() {
				if (i == 0) {
					MapManager mapManager = plugin.getMapManager();
					TeamManager teamManager = plugin.getTeamManager();
					/*if (plugin.getMapInfos().size() == 1) {
						for (Player all : Bukkit.getOnlinePlayers()) {
							all.kickPlayer("§c接続し直してください！");
						}
					}*/
					Bukkit.getScheduler().cancelTask(fireworkTaskId);
					String nextMap = next == null ? mapManager.getNextMap() : next;
					// mapManager.loadMap(nextMap);
					// Bukkit.broadcastMessage("§6次のマップを読み込んでいます...: §e" + nextMap);
					mapManager.setCurrentMap(nextMap);
					// Bukkit.broadcastMessage("§eマップを読み込みました。テレポートします...");
					MatchState.setState(MatchState.WAITING);
					KitManager kitManager = plugin.getKitManager();
					for (Player all : Bukkit.getOnlinePlayers()) {
						all.teleport(kitManager.getSpectatorSpawn());
						teamManager.setTeam(all, teamManager.getSpectatorTeam());
						plugin.getKitManager().setSpectatorItem(all);
						GuiTeamSelector.open(all);
						all.setGameMode(GameMode.CREATIVE);
						mapManager.sendMapInformation(all);
					}
					// GuiTeamSelector.update();
					GuiTeamSelector.init(plugin);
					i--;
				}
				if (i > 0) {
					if (i == maxcount || i % 10 == 0 || i == 15 || i == 10 || (i > 0 && i <= 5)) {
						Bukkit.broadcastMessage("§9次のマップに移動するまで後§b" + i + "§9秒...");
					}
					i--;
				}
			}
		}, 0L, 20L).getTaskId();
	}

	public void cancelStart() {
		if (!MatchState.isState(MatchState.COUNTDOWN))
			return;
		BukkitScheduler bs = plugin.getServer().getScheduler();
		bs.cancelTask(startingTaskId);
		Bukkit.broadcastMessage("§cゲーム開始がキャンセルされました！");
		MatchState.setState(MatchState.WAITING);
		for (Player all : Bukkit.getOnlinePlayers()) {
			all.playSound(all.getLocation(), Sound.CLICK, 1f, 1f);
		}
	}

	public void cancelCycling() {
		if (!MatchState.isState(MatchState.CYCLING))
			return;
		BukkitScheduler bs = plugin.getServer().getScheduler();
		bs.cancelTask(cyclingTaskId);
		Bukkit.broadcastMessage("§cマップ移動がキャンセルされました！");
		MatchState.setState(stateBeforeCycling);
		for (Player all : Bukkit.getOnlinePlayers()) {
			all.playSound(all.getLocation(), Sound.CLICK, 1f, 1f);
		}
	}

	public void spawn(Player p) {
		TeamManager teamManager = plugin.getTeamManager();
		KitManager kitManager = plugin.getKitManager();
		PvPTeam team = teamManager.getTeam(p);
		p.setFoodLevel(20);
		p.setSaturation(20);
		p.setHealth(20);
		p.setFallDistance(0);
		p.setFireTicks(0);
		p.setLevel(0);
		p.setExp(0f);
		if (team == teamManager.getSpectatorTeam()) {
			for (PotionEffect pot : p.getActivePotionEffects()) {
				p.removePotionEffect(pot.getType());
			}
			PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 60 * 60, 0);
			PotionEffect night_vision = new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60 * 60, 0);
			p.addPotionEffect(invisibility);
			p.addPotionEffect(night_vision);
			p.teleport(kitManager.getSpectatorSpawn());
		} else {
			for (PotionEffect pot : p.getActivePotionEffects()) {
				p.removePotionEffect(pot.getType());
			}
			p.teleport(kitManager.getSpawn(team));
			giveKit(p);
			p.setGameMode(GameMode.SURVIVAL);
			p.setAllowFlight(false);
		}
	}

	public void giveKit(Player p) {
		TeamManager teamManager = plugin.getTeamManager();
		KitManager kitManager = plugin.getKitManager();
		PvPTeam team = teamManager.getTeam(p);
		if (team == teamManager.getSpectatorTeam()) {
			kitManager.setSpectatorItem(p);
			return;
		}
		ItemStack air = new ItemStack(Material.AIR);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
		setContents(p, kitManager.getParentKit());
		setArmorContents(p, kitManager.getParentKitArmor());
		setContents(p, kitManager.getKitInventory(team));
		setArmorContents(p, kitManager.getKitArmor(team));
		p.updateInventory();
	}

	public void setContents(Player p, List<KitItem> kitItems) {
		for (KitItem ki : kitItems) {
			p.getInventory().setItem(ki.getSlot(), ki.getItemStack());
		}
	}

	public void setArmorContents(Player p, List<KitItem> kitItems) {
		for (KitItem ki : kitItems) {
			if (ki.getSlot() == 40)
				p.getInventory().setHelmet(ki.getItemStack());
			if (ki.getSlot() == 41)
				p.getInventory().setChestplate(ki.getItemStack());
			if (ki.getSlot() == 42)
				p.getInventory().setLeggings(ki.getItemStack());
			if (ki.getSlot() == 43)
				p.getInventory().setBoots(ki.getItemStack());
		}
	}

	public void setTeamGameType(TeamGameType teamGameType) {
		PluginManager pm = plugin.getServer().getPluginManager();
		if (this.teamGameType != null) { // TODO unregister all handlerlist
			if (this.teamGameType == TeamGameType.TDM) {
				HandlerList.unregisterAll(tdmCombatListener);
			}
			if (this.teamGameType == TeamGameType.DTM) {
				HandlerList.unregisterAll(dtmMonumentListener);
			}
			if (this.teamGameType == TeamGameType.CTW) {
				HandlerList.unregisterAll(ctwWoolListener);
			}
			if (this.teamGameType == TeamGameType.ANNI) {
				HandlerList.unregisterAll(anniNexusListener);
				HandlerList.unregisterAll(enderFurnaceListener);
				HandlerList.unregisterAll(anniResourceListener);
			}
		}
		this.teamGameType = teamGameType;
		plugin.setGameObjectiveManager(new GameObjectiveManager());
		if (teamGameType == TeamGameType.TDM) { // TODO Register Listeners each gamemode.
			tdmCombatListener = new TDMCombatListener(plugin);
			pm.registerEvents(tdmCombatListener, plugin);
			TDMConfig tdmConfig = (TDMConfig) plugin.getMapManager().getCurrentMapInfo().getMapConfig();
			plugin.setTdmScoreManager(new TDMScoreManager(tdmConfig.getMaxScore()));
		}
		if (teamGameType == TeamGameType.DTM) {
			plugin.setDtmMonumentManager(new DTMMonumentManager(plugin));
			dtmMonumentListener = new DTMMonumentListener(plugin);
			pm.registerEvents(dtmMonumentListener, plugin);
		}
		if (teamGameType == TeamGameType.CTW) {
			plugin.setCtwWoolManager(new CTWWoolManager(plugin));
			ctwWoolListener = new CTWWoolListener(plugin);
			pm.registerEvents(ctwWoolListener, plugin);
		}
		if (this.teamGameType == TeamGameType.ANNI) {
			plugin.setAnniLocationManager(new AnniLocationManager(plugin));
			anniNexusListener = new AnniNexusListener(plugin);
			enderFurnaceListener = new EnderFurnaceListener(plugin);
			anniResourceListener = new AnniResourceListener(plugin);
			pm.registerEvents(anniNexusListener, plugin);
			pm.registerEvents(enderFurnaceListener, plugin);
			pm.registerEvents(anniResourceListener, plugin);
		}
	}

	public TeamGameType getTeamGameType() {
		return teamGameType;
	}

	public int getDuration() {
		return duration;
	}

}
