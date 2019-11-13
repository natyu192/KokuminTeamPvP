package me.nucha.teampvp.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.GameManager;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.TeamManager;
import me.nucha.teampvp.game.objective.GameObjective;
import me.nucha.teampvp.game.objective.GameObjectiveManager;
import me.nucha.teampvp.game.objective.NexusObjective;
import me.nucha.teampvp.game.objective.WoolObjective;
import me.nucha.teampvp.listeners.tdm.TDMScoreManager;
import me.nucha.teampvp.map.region.MonumentRegion;

public class ScoreboardUtils {

	private static TeamPvP plugin;

	public static void plugin(TeamPvP plugin) {
		ScoreboardUtils.plugin = plugin;
	}

	public static void undisplayBoard(Player p) {
		p.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
	}

	public static void displayBoard(Player p) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = sb.registerNewObjective(p.getName(), "dummy");
		o.setDisplayName(ConfigUtil.scoreboard_title);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		plugin.getTeamManager().getSpectatorTeam().registerOnScoreboard(p);
		for (PvPTeam team : plugin.getTeamManager().getTeams()) {
			team.registerOnScoreboard(p);
		}
		p.setScoreboard(sb);
		updateBoard(p);
	}

	public static void replaceScore(Player p, int score, String str) {
		Scoreboard sb = p.getScoreboard();
		Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
		for (String entry : sb.getEntries()) {
			if (o.getScore(entry).getScore() == score) {
				sb.resetScores(entry);
			}
		}
		if (str.length() > 16) {
			String prefix = str.substring(0, 16);
			String name = "";
			String suffix = "";
			if (str.length() > 32) {
				name = str.substring(16, 32);
			} else {
				name = str.substring(16);
			}
			if (str.length() > 48) {
				suffix = str.substring(32, 48);
			} else {
				if (str.length() > 32) {
					suffix = str.substring(16, 32);
				} else {
					suffix = "";
				}
			}
			org.bukkit.scoreboard.Team team = getOrCreateTeam(p, name);
			team.setPrefix(prefix);
			team.setSuffix(suffix);
			team.addEntry(name);
			str = name;
		}
		o.getScore(str).setScore(score);
	}

	public static void replaceScore(Player p, int score, String str1, String str2, String str3) {
		org.bukkit.scoreboard.Team team = getOrCreateTeam(p, str2);
		team.setPrefix(str1);
		team.setSuffix(str3);
		team.addEntry(str2);
		replaceScore(p, score, str2);
	}

	public static void removeScore(Player p, String score) {
		try {
			Scoreboard sb = p.getScoreboard();
			sb.resetScores(score);
		} catch (Exception e) {

		}
	}

	public static void removeScore(Player p, int score) {
		try {
			Scoreboard sb = p.getScoreboard();
			Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
			for (String entry : sb.getEntries()) {
				if (o.getScore(entry).getScore() == score) {
					sb.resetScores(entry);
				}
			}
		} catch (Exception e) {

		}
	}

	public static void removeAllScores(Player p) {
		try {
			Scoreboard sb = p.getScoreboard();
			for (String entry : sb.getEntries()) {
				sb.resetScores(entry);
			}
		} catch (Exception e) {

		}
	}

	public static org.bukkit.scoreboard.Team getOrCreateTeam(Player p, String name) {
		org.bukkit.scoreboard.Team t = p.getScoreboard().getTeam(name);
		if (t == null) {
			if (name.length() > 16) {
				name = name.substring(0, 16);
			}
			t = p.getScoreboard().registerNewTeam(name);
		}
		return t;
	}

	public static void startTimerOnScoreboard(Player p, String text, double timercount, int line) {
		if (text.length() > 16) {
			System.out.println("The text cannot be more than 16");
			return;
		}
		org.bukkit.scoreboard.Team team = getOrCreateTeam(p, text);
		team.setPrefix("§e");
		team.setSuffix(timercount + "秒");
		team.addEntry(text);
		replaceScore(p, line, text);
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			double ndt = timercount;
			boolean cont = true;

			@Override
			public void run() {
				if (cont) {
					BigDecimal bd = new BigDecimal(ndt);
					double count = bd.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
					if (count <= 0.0) {
						p.getScoreboard().resetScores(text);
						cont = false;
						return;
					}
					team.setSuffix(count + "秒");
					ndt -= 0.1;
				}
			}
		}, 0L, 2L);
	}

	public static void updateBoard(Player p) {
		Scoreboard board = p.getScoreboard();
		Objective o = board.getObjective(DisplaySlot.SIDEBAR);
		GameManager gameManager = plugin.getGameManager();
		TeamManager teamManager = plugin.getTeamManager();
		GameObjectiveManager gameObjectiveManager = plugin.getGameObjectiveManager();
		String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		if (MatchState.isState(MatchState.WAITING) || MatchState.isState(MatchState.COUNTDOWN) || MatchState.isState(MatchState.CYCLING)) {
			removeAllScores(p);
			o.setDisplayName(ConfigUtil.scoreboard_title);
			int sbScore = 16;
			replaceScore(p, sbScore--, "", "§7", date);
			replaceScore(p, sbScore--, "            ");
			for (String mainline : ConfigUtil.scoreboard_main) {
				replaceScore(p, sbScore--, mainline);
			}
			replaceScore(p, sbScore--, "       ");
			replaceScore(p, sbScore--, "§a現在のゲームモード: ");
			replaceScore(p, sbScore--, " §b§l" + gameManager.getTeamGameType().getDisplayName());
			replaceScore(p, sbScore--, "    ");
			replaceScore(p, sbScore--, "§aあなたのチーム: ");
			replaceScore(p, sbScore--, "", " §f", ""); // TEAM
			if (teamManager.getTeam(p) != null) {
				getOrCreateTeam(p, " §f").setSuffix(teamManager.getTeam(p).getDisplayName());
			} else {
				getOrCreateTeam(p, " §f").setSuffix("§7読み込み中です...");
			}
			replaceScore(p, sbScore--, " ");
			replaceScore(p, sbScore--, "§aMap: §f" + plugin.getMapManager().getCurrentMap());
		}
		if (MatchState.isState(MatchState.IN_GAME) || MatchState.isState(MatchState.ENDING)) {
			o.setDisplayName("§e§l" + gameManager.getTeamGameType().getDisplayName());
			if (gameManager.getTeamGameType() == TeamGameType.TDM) {
				TDMScoreManager tdmScoreManager = plugin.getTdmScoreManager();
				removeAllScores(p);
				int sbScore = 16;
				replaceScore(p, sbScore--, "§7" + date);
				replaceScore(p, sbScore--, "§f§r");
				replaceScore(p, sbScore--, "§e", "Duration: §b", toMinAndSec(gameManager.getDuration()));
				replaceScore(p, sbScore--, "§e----- MAX §c" + tdmScoreManager.getMaxscore() + " §e-----");
				for (PvPTeam team : teamManager.getTeams()) {
					replaceScore(p, sbScore--, "", team.getDisplayName(), ": " + String.valueOf(tdmScoreManager.getScore(team)));
				}
				replaceScore(p, sbScore--, " ");
				replaceScore(p, sbScore--, "§aMap: §f" + plugin.getMapManager().getCurrentMap());
			}
			if (gameManager.getTeamGameType() == TeamGameType.DTM) {
				removeAllScores(p);
				List<MonumentRegion> monumentRegions = plugin.getDtmMonumentManager().getMonumentRegions();
				HashMap<PvPTeam, List<MonumentRegion>> objectives = new HashMap<>();
				for (PvPTeam team : teamManager.getTeams()) {
					objectives.put(team, new ArrayList<>());
				}
				for (MonumentRegion monumentRegion : monumentRegions) {
					objectives.get(monumentRegion.getOwnTeam()).add(monumentRegion);
				}
				int sbScore = 16;
				replaceScore(p, sbScore--, "§7" + date);
				replaceScore(p, sbScore--, "§f§r");
				for (PvPTeam team : objectives.keySet()) {
					replaceScore(p, sbScore--, team.getDisplayName());
					for (MonumentRegion monumentRegion : objectives.get(team)) {
						GameObjective objective = gameObjectiveManager.getObjective(
								monumentRegion.getName(), monumentRegion.getOwnTeam());
						objective.displayOnScoreboard(p, sbScore--);
					}
				}
				replaceScore(p, sbScore--, " ");
				replaceScore(p, sbScore--, "§aMap: §f" + plugin.getMapManager().getCurrentMap());
			}
			if (gameManager.getTeamGameType() == TeamGameType.CTW) {
				removeAllScores(p);
				List<WoolObjective> woolObjectives = plugin.getCtwWoolManager().getWoolObjectives();
				HashMap<PvPTeam, List<WoolObjective>> objectives = new HashMap<>();
				for (PvPTeam team : teamManager.getTeams()) {
					objectives.put(team, new ArrayList<>());
				}
				for (WoolObjective woolObjective : woolObjectives) {
					objectives.get(woolObjective.getOwnTeam()).add(woolObjective);
				}
				int sbScore = 16;
				replaceScore(p, sbScore--, "§7" + date);
				replaceScore(p, sbScore--, "§f§r");
				for (PvPTeam team : objectives.keySet()) {
					replaceScore(p, sbScore--, team.getDisplayName());
					for (WoolObjective woolObjective : objectives.get(team)) {
						GameObjective objective = gameObjectiveManager.getObjective(
								woolObjective.getDisplayName(), woolObjective.getOwnTeam());
						objective.displayOnScoreboard(p, sbScore--);
					}
				}
				replaceScore(p, sbScore--, " ");
				replaceScore(p, sbScore--, "§aMap: §f" + plugin.getMapManager().getCurrentMap());
			}
			if (gameManager.getTeamGameType() == TeamGameType.ANNI) {
				removeAllScores(p);
				List<NexusObjective> nexusObjectives = plugin.getAnniLocationManager().getNexusObjectives();
				HashMap<PvPTeam, List<NexusObjective>> objectives = new HashMap<>();
				for (PvPTeam team : teamManager.getTeams()) {
					objectives.put(team, new ArrayList<>());
				}
				for (NexusObjective nexusObjective : nexusObjectives) {
					objectives.get(nexusObjective.getOwnTeam()).add(nexusObjective);
				}
				int sbScore = 16;
				replaceScore(p, sbScore--, "§7" + date);
				replaceScore(p, sbScore--, "§f§r");
				for (PvPTeam team : objectives.keySet()) {
					replaceScore(p, sbScore--, team.getDisplayName());
					for (NexusObjective nexusObjective : objectives.get(team)) {
						GameObjective objective = gameObjectiveManager.getObjective(
								nexusObjective.getDisplayName(), nexusObjective.getOwnTeam());
						objective.displayOnScoreboard(p, sbScore--);
					}
				}
				replaceScore(p, sbScore--, " ");
				replaceScore(p, sbScore--, "§aMap: §f" + plugin.getMapManager().getCurrentMap());
			}
		}
	}

	public static void updateBoards() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			updateBoard(all);
		}
	}

	public static String toMinAndSec(int seconds) {
		int huni = seconds / 60;
		int byoui = seconds % 60;
		String hun = String.valueOf(huni);
		String byou = String.valueOf(byoui);
		if (hun.length() == 1) {
			hun = "0" + hun;
		}
		if (byou.length() == 1) {
			byou = "0" + byou;
		}
		return hun + ":" + byou;
	}

}
