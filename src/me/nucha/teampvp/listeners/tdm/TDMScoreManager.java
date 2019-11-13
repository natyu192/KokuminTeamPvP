package me.nucha.teampvp.listeners.tdm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.utils.ScoreboardUtils;

public class TDMScoreManager {

	private HashMap<PvPTeam, Integer> scores;
	private int maxscore;

	public TDMScoreManager(int maxscore) {
		this.scores = new HashMap<>();
		for (PvPTeam team : TeamPvP.getInstance().getTeamManager().getTeams()) {
			scores.put(team, 0);
		}
		this.maxscore = maxscore;
	}

	public void reset() {
		for (PvPTeam team : scores.keySet()) {
			scores.put(team, 0);
		}
		updateBoard();
	}

	public int getScore(PvPTeam team) {
		return scores.get(team);
	}

	public HashMap<PvPTeam, Integer> getScores() {
		return scores;
	}

	public void setScore(PvPTeam team, int score) {
		scores.put(team, score);
		updateBoard();
	}

	public void addScore(PvPTeam team, int amount) {
		scores.put(team, scores.get(team) + amount);
		updateBoard();
	}

	public void takeScore(PvPTeam team, int amount) {
		scores.put(team, scores.get(team) - amount);
		updateBoard();
	}

	public int getMaxscore() {
		return maxscore;
	}

	public List<PvPTeam> getTops() {
		int topscore = 0;
		List<PvPTeam> tops = new ArrayList<>();
		for (PvPTeam team : scores.keySet()) {
			// if (topscore > scores.get(team)) {
			if (topscore < scores.get(team)) {
				tops.clear();
				tops.add(team);
				topscore = scores.get(team);
			}
			if (topscore == scores.get(team)) {
				tops.add(team);
			}
		}
		return tops;
	}

	private void updateBoard() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			for (PvPTeam team : scores.keySet()) {
				Team sbTeam = ScoreboardUtils.getOrCreateTeam(all, team.getDisplayName());
				sbTeam.setSuffix(": " + String.valueOf(scores.get(team)));
			}
		}
	}

}
