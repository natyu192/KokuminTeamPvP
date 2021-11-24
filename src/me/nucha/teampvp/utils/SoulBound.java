package me.nucha.teampvp.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.nucha.teampvp.utils.ParticleEffect.ItemData;

public class SoulBound implements Listener {

	private static String sblore = "§6SoulBound";

	public static void setSoulBound(ItemStack item, boolean soulbound) {
		if (soulbound) {
			if (isSoulBound(item)) {
				return;
			}

			ItemMeta im = item.getItemMeta();
			List<String> lore = null;
			if (im.hasLore()) {
				lore = im.getLore();
			} else {
				lore = new ArrayList<String>();
			}
			lore.add(sblore);
			im.setLore(lore);
			item.setItemMeta(im);
		} else {
			if (!(isSoulBound(item))) {
				return;
			}
			if (!item.hasItemMeta()) {
				return;
			}
			if (!item.getItemMeta().hasLore()) {
				return;
			}

			ItemMeta im = item.getItemMeta();
			if (im.getLore().contains(sblore)) {
				List<String> lore_ = im.getLore();
				lore_.remove(sblore);
				im.setLore(lore_);
			}
			item.setItemMeta(im);
		}
	}

	public static boolean isSoulBound(ItemStack item) {
		try {
			if (item.getItemMeta().getLore().contains(sblore)) {
				return true;
			}
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player p = event.getPlayer();
		if (isSoulBound(event.getItemDrop().getItemStack())) {
			event.getItemDrop().remove();
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.playSound(p.getLocation(), Sound.ITEM_BREAK, 3, 3);
			}
			ItemStack item = event.getItemDrop().getItemStack();
			ItemData data = new ItemData(item.getType(), (byte) item.getDurability());
			Location l = p.getLocation();
			Vector d = l.getDirection();
			l.add(d.multiply(2));
			l.add(0, p.getEyeHeight(), 0);
			ParticleEffect.ITEM_CRACK.display(data, 0, 0, 0, 1, 20, l, 256);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		for (ItemStack drop : event.getDrops()) {
			if (isSoulBound(drop)) {
				drop.setType(Material.AIR);
			}
		}
	}

	@EventHandler
	public void onCraft(PrepareItemCraftEvent event) {
		for (ItemStack item : event.getInventory().getContents()) {
			if (isSoulBound(item)) {
				setSoulBound(event.getRecipe().getResult(), true);
			}
		}
	}

}
