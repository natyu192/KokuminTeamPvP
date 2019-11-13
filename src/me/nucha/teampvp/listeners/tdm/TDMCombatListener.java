package me.nucha.teampvp.listeners.tdm;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.GameManager;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.TeamManager;

public class TDMCombatListener implements Listener {

	private TeamPvP plugin;

	public TDMCombatListener(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (MatchState.isState(MatchState.IN_GAME) && plugin.getGameManager().getTeamGameType() == TeamGameType.TDM) {
			Player p = event.getEntity();
			TeamManager teamManager = plugin.getTeamManager();
			GameManager gameManager = plugin.getGameManager();
			PvPTeam team = teamManager.getTeam(p);
			if (team != teamManager.getSpectators()) {
				TDMScoreManager tdmScoreManager = plugin.getTdmScoreManager();
				int maxscore = tdmScoreManager.getMaxscore();
				Player k = p.getKiller();
				if (k == null) {
					tdmScoreManager.takeScore(team, 1);
				} else {
					tdmScoreManager.addScore(team, 1);
					if (tdmScoreManager.getScore(team) >= maxscore) {
						List<PvPTeam> winners = new ArrayList<>();
						winners.add(team);
						gameManager.endGame(winners);
					}
				}
			}
		}
	}
}
