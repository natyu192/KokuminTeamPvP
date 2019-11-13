package me.nucha.teampvp.game.objective;

import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.PvPTeam;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class GameObjective {

	private String displayName;
	private TeamGameType teamGameType;
	private GameObjectiveState state;
	private PvPTeam ownTeam;

	public GameObjective(String displayName, TeamGameType teamGameType, PvPTeam ownTeam) {
		this.displayName = displayName;
		this.teamGameType = teamGameType;
		this.state = GameObjectiveState.IN_COPLETE;
		this.ownTeam = ownTeam;
	}

	public String getDisplayName() {
		return displayName;
	}

	public TeamGameType getTeamGameType() {
		return teamGameType;
	}

	public GameObjectiveState getState() {
		return state;
	}

	public PvPTeam getOwnTeam() {
		return ownTeam;
	}

	public void setState(GameObjectiveState state) {
		this.state = state;
		updateStateOnScoreboard();
	}

	public void displayOnScoreboards(int score) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			displayOnScoreboard(all, score);
		}
	}

	public abstract void updateStateOnScoreboard();

	public abstract void displayOnScoreboard(Player p, int score);
}
