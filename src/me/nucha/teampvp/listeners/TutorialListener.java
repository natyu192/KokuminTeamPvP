package me.nucha.teampvp.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.map.MapTutorial;

public class TutorialListener implements Listener {

	private TeamPvP plugin;
	private static HashMap<Player, Integer> progress;
	private static List<MapTutorial> tutorials;
	private List<String> cooldown;

	public TutorialListener(TeamPvP plugin) {
		this.plugin = plugin;
		progress = new HashMap<>();
		cooldown = new ArrayList<>();
	}

	public static void setTutorials(List<MapTutorial> tutorials) {
		TutorialListener.tutorials = tutorials;
		progress = new HashMap<>();
		for (Player all : Bukkit.getOnlinePlayers()) {
			progress.put(all, 0);
		}
	}

	public static List<MapTutorial> getTutorials() {
		return tutorials;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		progress.put(event.getPlayer(), 0);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		progress.remove(event.getPlayer());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (cooldown.contains(p.getName())) {
			return;
		}
		if (event.getAction() != Action.PHYSICAL && plugin.getTeamManager().getSpectatorTeam().contains(p)
				|| !(MatchState.isState(MatchState.IN_GAME))) {
			ItemStack item = p.getItemInHand();
			if (item != null && item.getType() == Material.BOOK && !tutorials.isEmpty()) {
				MapTutorial tutorial = null;
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (tutorials.size() <= progress.get(p)) {
						return;
					}
					tutorial = tutorials.get(progress.get(p));
					progress.put(p, progress.get(p) + 1);
				}
				if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (progress.get(p) <= 1) {
						return;
					}
					tutorial = tutorials.get(progress.get(p) - 2);
					progress.put(p, progress.get(p) - 1);
				}
				if (tutorial != null) {
					sendTutorialTitle(p);
					tutorial.to(p);
					if (tutorial.getLocation() != null) {
						p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
					} else {
						p.playSound(p.getLocation(), Sound.CLICK, 1f, 1f);
					}
					String playerName = p.getName();
					cooldown.add(playerName);
					Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable() {
						@Override
						public void run() {
							cooldown.remove(playerName);
						}
					}, 20L);
				}
			}
		}
	}

	public void sendTutorialTitle(Player p) {
		String modoru = "←本を左クリックして戻る";
		String susumu = "本を右クリックして進む→";
		if (progress.get(p) <= 1) {
			int byteLength = modoru.length();
			modoru = "";
			for (int i = 0; i < byteLength; i++) {
				modoru += " ";
			}
		}
		if (tutorials.size() == progress.get(p)) {
			susumu = "";
		}
		p.sendMessage(" ");
		p.sendMessage("        §dマップチュートリアル§5[" + progress.get(p) + "/" + tutorials.size() + "]");
		p.sendMessage("§6" + modoru + "        §e" + susumu);
	}

}
