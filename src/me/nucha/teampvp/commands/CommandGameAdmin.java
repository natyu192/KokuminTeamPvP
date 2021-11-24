package me.nucha.teampvp.commands;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.stats.StatsCategory;
import me.nucha.teampvp.listeners.ChatListener;

public class CommandGameAdmin implements CommandExecutor {

	private TeamPvP plugin;
	private HashMap<String, OfflinePlayer> resetStatsConfirm;

	public CommandGameAdmin(TeamPvP plugin) {
		this.plugin = plugin;
		this.resetStatsConfirm = new HashMap<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 2 && args[0].equalsIgnoreCase("resetstats")) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new BukkitRunnable() {
				@Override
				public void run() {
					if (resetStatsConfirm.containsKey(sender.getName())) {
						OfflinePlayer target = resetStatsConfirm.get(sender.getName());
						String targetName = target.getName();
						if (targetName.equalsIgnoreCase(args[1])) {
							for (StatsCategory category : StatsCategory.values()) {
								plugin.getStatsManager().getStatsInfo(target).set(category, 0);
							}
							sender.sendMessage("§c§l" + target.getName() + " §eのStatsをリセットしました！！");
						} else {
							sender.sendMessage("§6" + targetName + " のStatsのリセットをキャンセルしました！");
						}
						resetStatsConfirm.remove(sender.getName());
						return;
					}
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
					if (target == null || !target.hasPlayedBefore()) {
						sender.sendMessage("§6" + args[1] + " §cはこのサーバーにログインしたことがありません");
						return;
					}
					String targetName = target.getName();
					sender.sendMessage("§c§l本当に §4§l" + targetName + " §c§lのStatsをリセットしますか？");
					sender.sendMessage("§e§lリセットする場合、もう一度同じコマンドを入力してください！！");
					resetStatsConfirm.put(sender.getName(), target);
				}
			});
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
			if (args[0].equalsIgnoreCase("spy")) {
				if (!(sender instanceof Player)) {
					return true;
				}
				Player p = (Player) sender;
				if (!plugin.getTeamManager().getSpectators().contains(p)) {
					sender.sendMessage("§c試合に参加している状態ではChat Spyを有効化できません");
					return true;
				}
				if (ChatListener.toggleChatSpy(p)) {
					sender.sendMessage("§5Chat Spyが§d有効化§5されました");
				} else {
					sender.sendMessage("§5Chat Spyが§6無効化§5されました");
				}
				return true;
			}
		}
		if (MatchState.isState(MatchState.IN_GAME)) {
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
			}
			sender.sendMessage("§6------------ §eゲーム管理コマンド §6------------");
			sender.sendMessage("§d/gameadmin point [team] [amount] §6--- 得点を確認したり、増減させます");
		}
		sender.sendMessage("§e/gameadmin teams §6--- 全てのチーム名と ID を表示します");
		sender.sendMessage("§e/gameadmin spy §6--- 観戦、チーム含めた全てのチャットを盗聴します");
		sender.sendMessage("§e/gameadmin resetstats <player> §6--- プレイヤーの戦績をリセットします");
		return true;
	}

}
