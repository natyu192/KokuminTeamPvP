package me.nucha.teampvp.game.objective;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.NumberConversions;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.map.region.MonumentRegion;
import me.nucha.teampvp.utils.MathUtils;
import me.nucha.teampvp.utils.ScoreboardUtils;
import me.nucha.teampvp.utils.SymbolUtils;

public class MonumentObjective extends GameObjective {

	private MonumentRegion monumentRegion;
	private HashMap<String, Integer> breakcount;

	public MonumentObjective(String displayName, TeamGameType teamGameType, PvPTeam ownTeam, MonumentRegion monumentRegion) {
		super(displayName, teamGameType, ownTeam);
		this.monumentRegion = monumentRegion;
		this.breakcount = new HashMap<>();
	}

	public MonumentRegion getMonumentRegion() {
		return monumentRegion;
	}

	public void breakPieceOfMonument(Player p) {
		if (breakcount.containsKey(p.getName())) {
			breakcount.put(p.getName(), breakcount.get(p.getName()) + 1);
		} else {
			breakcount.put(p.getName(), 1);
		}
	}

	public HashMap<String, Integer> getBreakCount() {
		return breakcount;
	}

	public double getPercentageOfContribute(Player p) {
		return getPercentageOfContribute(p.getName());
	}

	public double getPercentageOfContribute(String name) {
		if (!breakcount.containsKey(name)) {
			return 0;
		} else {
			int max = 0;
			for (Material b : monumentRegion.getDefaultBlocks().values()) {
				if (b != Material.AIR) {
					max++;
				}
			}
			double percentage = MathUtils.percentage(max, breakcount.get(name), 1);
			return percentage;
		}
	}

	@Override
	public void updateStateOnScoreboard() {
		Bukkit.getScheduler().runTask(TeamPvP.getInstance(), () -> {
			int max = 0;
			int value = 0;
			for (Location l : monumentRegion.getBlocks()) {
				if (monumentRegion.getDefaultBlocks().get(l) != Material.AIR) {
					max++;
				}
				if (monumentRegion.isBroken(l)) {
					value++;
				}
			}
			for (Player all : Bukkit.getOnlinePlayers()) {
				String localTeamName = getDisplayName();
				int percentage = NumberConversions.floor(MathUtils.percentage(max, value));
				String localTeamSuffix = " §e" + percentage + "%§r";
				if (percentage == 0) {
					localTeamSuffix = " §c" + percentage + "%§r";
				}
				if (percentage == 100) {
					localTeamSuffix = " §a" + percentage + "%§r";
				}
				Team localTeam = ScoreboardUtils.getOrCreateTeam(all, localTeamName + getOwnTeam().getColor());
				if (getState() == GameObjectiveState.IN_COPLETE) {
					localTeam.setPrefix(" §c" + SymbolUtils.x() + " §r");
				}
				if (getState() == GameObjectiveState.SEMI_COMPLETED) {
					localTeam.setPrefix(" §e" + SymbolUtils.star(2) + " §r");
				}
				if (getState() == GameObjectiveState.COPLETED) {
					localTeam.setPrefix(" §a" + SymbolUtils.check() + " §r");
					localTeamSuffix = " §a100%§r";
				}
				localTeam.setSuffix(localTeamSuffix);
			}
		});
	}

	@Override
	public void displayOnScoreboard(Player p, int score) {
		int max = 0;
		int value = 0;
		for (Location l : monumentRegion.getBlocks()) {
			if (monumentRegion.getDefaultBlocks().get(l) != Material.AIR) {
				max++;
			}
			if (monumentRegion.isBroken(l)) {
				value++;
			}
		}
		String localTeamName = getDisplayName();
		int percentage = NumberConversions.floor(MathUtils.percentage(max, value));
		String localTeamSuffix = " §e" + percentage + "%§r";
		if (percentage == 0) {
			localTeamSuffix = " §c" + percentage + "%§r";
		}
		if (percentage == 100) {
			localTeamSuffix = " §a" + percentage + "%§r";
		}
		ScoreboardUtils.replaceScore(p, score, "", localTeamName + getOwnTeam().getColor(), "");
		Team localTeam = ScoreboardUtils.getOrCreateTeam(p, localTeamName + getOwnTeam().getColor());
		if (getState() == GameObjectiveState.IN_COPLETE) {
			localTeam.setPrefix(" §c" + SymbolUtils.x() + " §r");
		}
		if (getState() == GameObjectiveState.SEMI_COMPLETED) {
			localTeam.setPrefix(" §e" + SymbolUtils.star(2) + " §r");
		}
		if (getState() == GameObjectiveState.COPLETED) {
			localTeam.setPrefix(" §a" + SymbolUtils.check() + " §r");
			localTeamSuffix = " §a100%§r";
		}
		localTeam.setSuffix(localTeamSuffix);
	}
}
