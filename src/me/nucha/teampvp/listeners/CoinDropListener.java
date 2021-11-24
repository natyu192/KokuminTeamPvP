package me.nucha.teampvp.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.teampvp.TeamPvP;

public class CoinDropListener implements Listener {

	private static TeamPvP plugin;
	private static List<Integer> dropIds;

	public CoinDropListener(TeamPvP plugin) {
		CoinDropListener.plugin = plugin;
		dropIds = new ArrayList<>();
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (dropIds.contains(event.getItem().getEntityId())) {
			event.setCancelled(true);
		}
	}

	public static void drop(Player p, Location l) {
		ItemStack gold = new ItemStack(Material.GOLD_NUGGET);
		List<Item> dropped = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ItemStack goldClone = gold.clone();
			ItemMeta goldMeta = goldClone.getItemMeta();
			goldMeta.setDisplayName("Item" + new Random().nextInt(9999));
			goldClone.setItemMeta(goldMeta);
			Item item = l.getWorld().dropItemNaturally(l, goldClone);
			dropIds.add(item.getEntityId());
			dropped.add(item);
		}
		BukkitRunnable removeTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (Item item : dropped) {
					dropIds.remove(new Integer(item.getEntityId()));
					item.remove();
				}
			}
		};
		removeTask.runTaskLater(plugin, 60L);
		BukkitRunnable soundTask = new BukkitRunnable() {
			float i = 5;

			@Override
			public void run() {
				if (i > 0) {
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1f, 1 + (i / 5));
					i--;
				} else {
					cancel();
				}
			}
		};
		soundTask.runTaskTimer(plugin, 1L, 1L);
	}

}
