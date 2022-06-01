package me.nucha.teampvp.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.japanize.JapanizeType;

import me.nucha.core.sql.PrefixManager;
import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamManager;

public class ChatListener implements Listener {

	private static TeamPvP plugin;
	private static List<Player> chatSpy;

	public ChatListener(TeamPvP plugin) {
		ChatListener.plugin = plugin;
		ChatListener.chatSpy = new ArrayList<>();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player p = event.getPlayer();
		TeamManager teamManager = plugin.getTeamManager();
		PvPTeam team = teamManager.getTeam(p);
		String prefix = TeamPvP.pl_kokuminserver ? PrefixManager.getPrefix(p.getUniqueId()) + "§r" : "";
		String playerName = prefix + team.getColor() + p.getName();
		boolean exclamation = event.getMessage().startsWith("!") || event.getMessage().startsWith("！");
		boolean ended = MatchState.isState(MatchState.CYCLING) || MatchState.isState(MatchState.ENDING);
		if (exclamation) {
			event.setMessage(japanize(event.getMessage().substring(1)));
		}
		if (ended || exclamation) {
			Bukkit.broadcastMessage("<" + playerName + "§r>: " + event.getMessage());
		} else {
			event.setMessage(japanize(event.getMessage()));
			String teamName = team == teamManager.getSpectatorTeam() ? "Spectator" : "Team";
			String message = team.getColor().toString() + "[" + teamName + team.getColor().toString() + "] §r<"
					+ playerName + "§r>: " + event.getMessage();
			TeamPvP.getConsoleSender().sendMessage(message);
			for (Player teamMemberOrSpy : Bukkit.getOnlinePlayers()) {
				if (chatSpy.contains(teamMemberOrSpy)) {
					teamMemberOrSpy.sendMessage("§7(Spy) §r" + message);
				} else if (team.getTeamMembers().contains(teamMemberOrSpy)) {
					teamMemberOrSpy.sendMessage(message);
				}
			}
		}
		event.setCancelled(true);
	}

	private static String japanize(String message) {
		if (!TeamPvP.pl_lunachat) {
			return message;
		}
		if (message.startsWith("#") || message.startsWith("＃")) {
			return message.substring(1);
		} else {
			return LunaChat.getInstance().getLunaChatAPI().japanize(message, JapanizeType.KANA);
		}
	}

	public static boolean toggleChatSpy(Player p) {
		if (chatSpy.contains(p)) {
			chatSpy.remove(p);
			return false;
		} else {
			chatSpy.add(p);
			return true;
		}
	}

}
