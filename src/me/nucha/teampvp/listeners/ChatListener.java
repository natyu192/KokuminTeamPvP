package me.nucha.teampvp.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.japanize.JapanizeType;

import me.nucha.core.sql.SQLManager;
import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamManager;

public class ChatListener implements Listener {

	private static TeamPvP plugin;
	private static List<Player> chatSpy;
	private static HashMap<UUID, String> prefixes;

	public ChatListener(TeamPvP plugin) {
		ChatListener.plugin = plugin;
		ChatListener.chatSpy = new ArrayList<>();
		prefixes = new HashMap<>();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new BukkitRunnable() {
			@Override
			public void run() {
				String prefix = SQLManager.getPrefix(uuid, true);
				prefix = ChatColor.translateAlternateColorCodes('&', prefix);
				prefixes.put(uuid, prefix);
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public static void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player p = event.getPlayer();
		String prefix = prefixes.containsKey(p.getUniqueId()) ? prefixes.get(p.getUniqueId()) : "";
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
			Bukkit.broadcastMessage("<" + prefix + p.getDisplayName() + "§r>: " + event.getMessage());
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
					+ prefix + p.getDisplayName() + "§r>: " + event.getMessage();
			TeamPvP.getConsoleSender().sendMessage(message);
			for (Player teamMemberOrSpy : Bukkit.getOnlinePlayers()) {
				if (chatSpy.contains(teamMemberOrSpy)) {
					teamMemberOrSpy.sendMessage("§7(Spy) §r" + message);
				} else if (team.getTeamMembers().contains(teamMemberOrSpy)) {
					teamMemberOrSpy.sendMessage(message);
				}
			}
			event.setCancelled(true);
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
