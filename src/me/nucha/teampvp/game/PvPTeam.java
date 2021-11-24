package me.nucha.teampvp.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.utils.ScoreboardUtils;

public class PvPTeam {

	private String name;
	private String id;
	private ChatColor color;
	private int max;
	private List<Player> teamMembers;

	public PvPTeam(ChatColor color, String name, String id, int max) {
		this.name = name;
		this.id = id;
		this.color = color;
		this.max = max;
		this.teamMembers = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			registerOnScoreboard(p);
		}
	}

	public String getDisplayName() {
		return color.toString() + name + "§r";
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public ChatColor getColor() {
		return color;
	}

	public int getMax() {
		return max;
	}

	public List<Player> getTeamMembers() {
		return teamMembers;
	}

	public boolean join(Player p) {
		if (teamMembers.size() >= max || teamMembers.contains(p)) {
			return false;
		}
		p.setDisplayName(color + p.getName() + "§r");
		((CraftPlayer) p).spigot().setCollidesWithEntities(true);
		TeamManager teamManager = TeamPvP.getInstance().getTeamManager();
		PvPTeam teamspec = teamManager.getSpectatorTeam();
		PvPTeam beforeTeam = teamManager.getTeam(p);
		if (beforeTeam != null) {
			beforeTeam.leave(p, false);
		}
		teamMembers.add(p);
		for (Player all : Bukkit.getOnlinePlayers()) {
			Team sbTeam = getScoreboardTeam(all);
			sbTeam.addPlayer(p);
			if (MatchState.isState(MatchState.IN_GAME) && p != all) {
				if (teamspec.contains(p)) {
					p.hidePlayer(all);
				} else {
					all.showPlayer(p);
				}
			}
		}
		return true;
	}

	public void leave(Player p, boolean beSpectator) {
		if (!teamMembers.contains(p)) {
			return;
		}
		teamMembers.remove(p);
		if (p.getActivePotionEffects() != null) {
			for (PotionEffect pot : p.getActivePotionEffects()) {
				p.removePotionEffect(pot.getType());
			}
		}
		for (Player all : Bukkit.getOnlinePlayers()) {
			Team sbTeam = getScoreboardTeam(all);
			sbTeam.removePlayer(p);
		}
		if (beSpectator) {
			TeamPvP.getInstance().getTeamManager().getSpectatorTeam().join(p);
		}
	}

	public void leaveAll() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			Team sbTeam = getScoreboardTeam(all);
			for (Player teamMember : teamMembers) {
				sbTeam.removePlayer(teamMember);
			}
			TeamPvP.getInstance().getTeamManager().getSpectatorTeam().join(all);
		}
		teamMembers.clear();
	}

	public void registerOnScoreboard(Player p) {
		Team sbTeam = getScoreboardTeam(p);
		sbTeam.setPrefix(color.toString());
		sbTeam.setSuffix(ChatColor.RESET.toString());
		sbTeam.setDisplayName(name);
		sbTeam.setAllowFriendlyFire(false);
		sbTeam.setCanSeeFriendlyInvisibles(true);
		for (Player teamMember : teamMembers) {
			sbTeam.addPlayer(teamMember);
		}
	}

	public Team getScoreboardTeam(Player p) {
		return ScoreboardUtils.getOrCreateTeam(p, id);
	}

	public boolean contains(Player p) {
		return teamMembers.contains(p);
	}

	public int size() {
		return teamMembers.size();
	}
}
