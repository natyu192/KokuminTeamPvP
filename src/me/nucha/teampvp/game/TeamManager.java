package me.nucha.teampvp.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.listeners.GuiTeamSelector;
import me.nucha.teampvp.utils.ScoreboardUtils;

public class TeamManager {

	private TeamPvP plugin;
	private int needPlayersToStart;
	private List<PvPTeam> teams;
	private PvPTeam spectatorTeam;
	private HashMap<UUID, PvPTeam> lastTeam;

	public TeamManager(TeamPvP plugin) {
		this.plugin = plugin;
		this.needPlayersToStart = 2;
		this.teams = new ArrayList<>();
		this.lastTeam = new HashMap<>();
		this.spectatorTeam = new PvPTeam(ChatColor.AQUA, "Spectator", "thespectatorteam", Bukkit.getMaxPlayers());
	}

	public void joinRandom(Player p) {
		if (!spectatorTeam.contains(p)) {
			return;
		}
		if (lastTeam.containsKey(p.getUniqueId())) {
			joinTeam(p, lastTeam.get(p.getUniqueId()));
			return;
		}
		List<PvPTeam> minimumTeams = new ArrayList<>();
		int minimumSize = 999;
		for (PvPTeam team : teams) {
			if (team.size() == team.getMax()) {
				continue;
			}
			if (team.size() < minimumSize) {
				minimumTeams.clear();
				minimumTeams.add(team);
				minimumSize = team.size();
				continue;
			}
			if (team.size() == minimumSize) {
				minimumTeams.add(team);
				continue;
			}
		}
		/*for (PvPTeam team : teams) {
			if (minimumTeam == null || team.size() > minimumTeam.size()) {
				minimumTeam = team;
				continue;
			}
		}*/
		PvPTeam minimumTeam = minimumTeams.get(new Random().nextInt(minimumTeams.size()));
		joinTeam(p, minimumTeam);
	}

	public void joinTeam(Player p, PvPTeam team) {
		if (MatchState.isState(MatchState.ENDING))
			return;
		if (team != spectatorTeam) {
			setTeam(p, team);
			if (MatchState.isState(MatchState.WAITING)) {
				if (total() >= needPlayersToStart) {
					plugin.getGameManager().startGame();
				} else {
					Bukkit.broadcastMessage("§aゲームを始めるには後§c" + (needPlayersToStart - total()) + "§a人必要です");
				}
			} else {
				lastTeam.put(p.getUniqueId(), team);
			}
			if (MatchState.isState(MatchState.IN_GAME)) {
				NavigatorManager.setAllowNavigator(p, false);
				plugin.getGameManager().spawn(p);
				plugin.getMapManager().sendMapDescription(p);
			}
		}
	}

	public void leaveTeam(Player p) {
		setTeam(p, spectatorTeam);
		if (MatchState.isState(MatchState.COUNTDOWN)) {
			if (total() < needPlayersToStart) {
				plugin.getGameManager().cancelStart();
			}
		}
		if (MatchState.isState(MatchState.WAITING) || MatchState.isState(MatchState.COUNTDOWN)) {
			ScoreboardUtils.getOrCreateTeam(p, " §f").setSuffix(getTeam(p).getDisplayName());
		}
	}

	public void quit(Player p) {
		getTeam(p).leave(p, false);
		if (MatchState.isState(MatchState.COUNTDOWN)) {
			if (total() < needPlayersToStart) {
				plugin.getGameManager().cancelStart();
			}
		}
		GuiTeamSelector.update();
	}

	public List<PvPTeam> getTeams() {
		return teams;
	}

	public void setTeam(Player p, PvPTeam team) {
		boolean success = team.join(p);
		if (success) {
			if (team.equals(spectatorTeam)) {
				if (p.getActivePotionEffects() != null) {
					for (PotionEffect pot : p.getActivePotionEffects()) {
						p.removePotionEffect(pot.getType());
					}
				}
				PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 60 * 60, 0);
				PotionEffect night_vision = new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60 * 60, 0);
				p.addPotionEffect(invisibility);
				p.addPotionEffect(night_vision);
				((CraftPlayer) p).spigot().setCollidesWithEntities(false);
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (MatchState.isState(MatchState.IN_GAME) && p != all) {
						p.showPlayer(all);
					}
				}
			} else {
				((CraftPlayer) p).spigot().setCollidesWithEntities(true);
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (MatchState.isState(MatchState.IN_GAME) && p != all) {
						if (getTeam(all).equals(spectatorTeam)) {
							p.hidePlayer(all);
						} else {
							all.showPlayer(p);
						}
					}
				}
			}
			if (MatchState.isState(MatchState.WAITING) || MatchState.isState(MatchState.COUNTDOWN)) {
				ScoreboardUtils.getOrCreateTeam(p, " §f").setSuffix(getTeam(p).getDisplayName());
			}
		}
	}

	public PvPTeam getSpectatorTeam() {
		return spectatorTeam;
	}

	public List<Player> getGamePlayers() {
		List<Player> players = new ArrayList<>();
		for (PvPTeam team : teams) {
			for (Player oPlayer : team.getTeamMembers()) {
				players.add(oPlayer);
			}
		}
		return players;
	}

	public List<Player> getSpectators() {
		return spectatorTeam.getTeamMembers();
	}

	public PvPTeam getTeam(Player p) {
		if (spectatorTeam.contains(p)) {
			return spectatorTeam;
		}
		for (PvPTeam team : teams) {
			if (team.contains(p)) {
				return team;
			}
		}
		return null;
	}

	public PvPTeam getTeamById(String id) {
		for (PvPTeam team : teams) {
			if (team.getId().equals(id)) {
				return team;
			}
		}
		return null;
	}

	public int total() {
		int total = 0;
		for (PvPTeam team : teams) {
			total += team.size();
		}
		return total;
	}

	public void registerTeam(ChatColor color, String name, String id, int max) {
		if (getTeamById(id) != null) {
			return;
		}
		PvPTeam team = new PvPTeam(color, name, id, max);
		teams.add(team);
	}

	public void unregisterTeam(String id) {
		PvPTeam team = getTeamById(id);
		if (team != null) {
			team.leaveAll();
			teams.remove(team);
		}
	}

	public void unregisterAllTeam() {
		List<PvPTeam> teamsClone = new ArrayList<>(teams);
		for (PvPTeam team : teamsClone) {
			unregisterTeam(team.getId());
		}
		lastTeam.clear();
	}

	public void leaveAll() {
		for (PvPTeam team : teams) {
			team.leaveAll();
		}
	}

}
