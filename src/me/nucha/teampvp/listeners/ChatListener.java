package me.nucha.teampvp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.japanize.JapanizeType;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamManager;

public class ChatListener implements Listener {

	private static TeamPvP plugin;

	public ChatListener(TeamPvP plugin) {
		ChatListener.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public static void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player p = event.getPlayer();
		TeamManager teamManager = plugin.getTeamManager();
		PvPTeam team = teamManager.getTeam(p);
		if (event.getMessage().startsWith("!") || event.getMessage().startsWith("！")) {
			event.setMessage(event.getMessage().substring(1));
			if (TeamPvP.pl_lunachat) {
				if (event.getMessage().startsWith("#") || event.getMessage().startsWith("＃")) {
					event.setMessage(event.getMessage().substring(1));
				} else {
					event.setMessage(LunaChat.getInstance().getLunaChatAPI().japanize(event.getMessage(), JapanizeType.KANA));
				}
			}
			// event.setFormat("<" + p.getDisplayName() + "§r>: " + event.getMessage());
			Bukkit.broadcastMessage("<" + p.getDisplayName() + "§r>: " + event.getMessage());
			event.setCancelled(true);
		} else {
			if (TeamPvP.pl_lunachat) {
				if (event.getMessage().startsWith("#") || event.getMessage().startsWith("＃")) {
					event.setMessage(event.getMessage().substring(1));
				} else {
					event.setMessage(LunaChat.getInstance().getLunaChatAPI().japanize(event.getMessage(), JapanizeType.KANA));
				}
			}
			String teamName = "Team";
			if (team == teamManager.getSpectatorTeam()) {
				teamName = "Spectator";
			}
			String message = team.getColor().toString() + "[" + teamName + team.getColor().toString() + "] §r<"
					+ p.getDisplayName() + "§r>: " + event.getMessage();
			TeamPvP.getConsoleSender().sendMessage(message);
			for (Player teamMember : team.getTeamMembers()) {
				if (teamMember != null) {
					teamMember.sendMessage(message);
				}
			}
			event.setCancelled(true);
		}
	}

}
