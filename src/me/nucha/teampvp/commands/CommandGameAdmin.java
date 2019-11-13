package me.nucha.teampvp.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;

public class CommandGameAdmin implements CommandExecutor {

	private TeamPvP plugin;

	public CommandGameAdmin(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!MatchState.isState(MatchState.IN_GAME)) {
			sender.sendMessage("§6------------ §eゲーム管理コマンド §6------------");
			sender.sendMessage("§eこのコマンドは、試合中にチームの得点などを操作できるコマンドです。");
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("teams")) {
				sender.sendMessage("§6------------ §eチーム一覧 §6------------");
				for (PvPTeam team : plugin.getTeamManager().getTeams()) {
					sender.sendMessage(team.getDisplayName() + " - " + team.getId());
				}
				return true;
			}
		}
		TeamGameType gameType = plugin.getGameManager().getTeamGameType();
		if (gameType.equals(TeamGameType.TDM)) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("point")) {
					sender.sendMessage("§6------------ §eTDMのスコア §6------------");
					for (PvPTeam team : plugin.getTeamManager().getTeams()) {
						int score = plugin.getTdmScoreManager().getScore(team);
						sender.sendMessage(team.getDisplayName() + ": " + score + " 点");
					}
					return true;
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("point")) {
					String teamId = args[1];
					PvPTeam team = plugin.getTeamManager().getTeamById(teamId);
					if (team == null) {
						sender.sendMessage("§6" + teamId + " §cという ID のチームがありません");
						return true;
					}
					boolean minus = false;
					if (args[2].charAt(0) == '-') {
						minus = true;
						args[2] = args[2].substring(1);
					} else if (args[2].charAt(0) == '+') {
						args[2] = args[2].substring(1);
					}
					if (!StringUtils.isNumeric(args[2])) {
						sender.sendMessage("§camount は数字で指定してください");
						return true;
					}
					int amount = Integer.valueOf(args[2]);
					if (amount == 0) {
						sender.sendMessage("§camount は 0 以外で指定してください");
						return true;
					}
					if (!minus) {
						plugin.getTdmScoreManager().addScore(team, amount);
						sender.sendMessage(team.getDisplayName() + " のスコアを §a+" + amount + " §rしました");
					} else {
						plugin.getTdmScoreManager().takeScore(team, amount);
						sender.sendMessage(team.getDisplayName() + " のスコアを §c-" + amount + " §rしました");
					}
					return true;
				}
			}
			sender.sendMessage("§6------------ §eゲーム管理コマンド §6------------");
			sender.sendMessage("§e/gameadmin point [team] [amount] §6--- 得点を確認したり、増減させます");
		}
		sender.sendMessage("§e/gameadmin teams §6--- 全てのチーム名と ID を表示します");
		return true;
	}

}
