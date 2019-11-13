package me.nucha.teampvp.game;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.utils.ScoreboardUtils;

public enum MatchState {
	WAITING, IN_GAME, COUNTDOWN, ENDING, CYCLING;
	private static MatchState currentState;

	public static void setState(MatchState state) {
		MatchState.currentState = state;

		if (state == WAITING) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				((CraftPlayer) all).spigot().setCollidesWithEntities(false);
			}
			ScoreboardUtils.updateBoards();
		}
		if (state == IN_GAME) {
			for (Player all : TeamPvP.getInstance().getTeamManager().getGamePlayers()) {
				((CraftPlayer) all).spigot().setCollidesWithEntities(true);
			}
			ScoreboardUtils.updateBoards();
		}
	}

	public static boolean isState(MatchState state) {
		return MatchState.currentState == state;
	}

	public static MatchState getState() {
		return currentState;
	}
}
