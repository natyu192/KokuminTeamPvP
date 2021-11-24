package me.nucha.teampvp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.GameManager;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.objective.GameObjective;
import me.nucha.teampvp.game.objective.NexusObjective;
import me.nucha.teampvp.game.objective.WoolObjective;
import me.nucha.teampvp.map.region.MonumentRegion;
import me.nucha.teampvp.utils.ScoreboardUtils;

public class CommandMatch implements CommandExecutor {

	private TeamPvP plugin;

	public CommandMatch(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		GameManager game = plugin.getGameManager();
		if (MatchState.isState(MatchState.IN_GAME) || MatchState.isState(MatchState.ENDING)) {
			String teams = "";
			for (PvPTeam team : plugin.getTeamManager().getTeams()) {
				String teamString = team.getDisplayName() + "§7(" + team.getTeamMembers().size() + "/" + team.getMax() + ")";
				;
				if (teams.isEmpty()) {
					teams = teamString;
				} else {
					teams += "§8, §r" + teamString;
				}
			}
			PvPTeam specTeam = plugin.getTeamManager().getSpectatorTeam();
			teams += " §8| " + specTeam.getDisplayName() + "§7(" + specTeam.getTeamMembers().size() + ")";
			sender.sendMessage("§5--------------- §d現在のマッチ§5 ---------------");
			sender.sendMessage("§e" + game.getTeamGameType().getDisplayName() + " §8| §b" + ScoreboardUtils.toMinAndSec(game.getDuration()));
			sender.sendMessage("§dチーム: " + teams);
			if (game.getTeamGameType() == TeamGameType.TDM) {
				sender.sendMessage("§dスコア: ");
				for (PvPTeam team : plugin.getTeamManager().getTeams()) {
					sender.sendMessage("  " + team.getDisplayName() + "§r: " + plugin.getTdmScoreManager().getScore(team));
				}
			} else if (game.getTeamGameType() == TeamGameType.DTM) {
				sender.sendMessage("§dモニュメント破壊状況: ");
				List<MonumentRegion> monumentRegions = plugin.getDtmMonumentManager().getMonumentRegions();
				HashMap<PvPTeam, List<MonumentRegion>> objectives = new HashMap<>();
				for (PvPTeam team : plugin.getTeamManager().getTeams()) {
					objectives.put(team, new ArrayList<>());
				}
				for (MonumentRegion monumentRegion : monumentRegions) {
					objectives.get(monumentRegion.getOwnTeam()).add(monumentRegion);
				}
				for (PvPTeam team : objectives.keySet()) {
					sender.sendMessage("  " + team.getDisplayName());
					for (MonumentRegion monumentRegion : objectives.get(team)) {
						GameObjective objective = plugin.getGameObjectiveManager().getObjective(
								monumentRegion.getName(), monumentRegion.getOwnTeam());
						sender.sendMessage("    " + objective.getText());
					}
				}
			} else if (game.getTeamGameType() == TeamGameType.CTW) {
				sender.sendMessage("§d羊毛の取得/設置状況: ");
				List<WoolObjective> woolObjectives = plugin.getCtwWoolManager().getWoolObjectives();
				HashMap<PvPTeam, List<WoolObjective>> objectives = new HashMap<>();
				for (PvPTeam team : plugin.getTeamManager().getTeams()) {
					objectives.put(team, new ArrayList<>());
				}
				for (WoolObjective woolObjective : woolObjectives) {
					objectives.get(woolObjective.getOwnTeam()).add(woolObjective);
				}
				for (PvPTeam team : objectives.keySet()) {
					sender.sendMessage("  " + team.getDisplayName());
					for (WoolObjective woolObjective : objectives.get(team)) {
						GameObjective objective = plugin.getGameObjectiveManager().getObjective(
								woolObjective.getDisplayName(), woolObjective.getOwnTeam());
						sender.sendMessage("    " + objective.getText());
					}
				}
			} else if (game.getTeamGameType() == TeamGameType.ANNI) {
				sender.sendMessage("§dネクサスのHP: ");
				List<NexusObjective> nexusObjectives = plugin.getAnniLocationManager().getNexusObjectives();
				HashMap<PvPTeam, List<NexusObjective>> objectives = new HashMap<>();
				for (PvPTeam team : plugin.getTeamManager().getTeams()) {
					objectives.put(team, new ArrayList<>());
				}
				for (NexusObjective nexusObjective : nexusObjectives) {
					objectives.get(nexusObjective.getOwnTeam()).add(nexusObjective);
				}
				for (PvPTeam team : objectives.keySet()) {
					sender.sendMessage("  " + team.getDisplayName());
					for (NexusObjective nexusObjective : objectives.get(team)) {
						GameObjective objective = plugin.getGameObjectiveManager().getObjective(
								nexusObjective.getDisplayName(), nexusObjective.getOwnTeam());
						sender.sendMessage("    " + objective.getText());
					}
				}
			}
		}
		return true;
	}

}
