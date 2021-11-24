package me.nucha.teampvp.listeners.anni;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.TeamGameType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class AnniResourceListener implements Listener {

	private TeamPvP plugin;
	private List<AnniResource> resources;
	private List<Location> blockCt;

	public AnniResourceListener(TeamPvP plugin) {
		this.plugin = plugin;
		this.resources = new ArrayList<>();
		this.blockCt = new ArrayList<>();
		resources.add(new AnniResource(Material.MELON_BLOCK, new ItemStack(Material.MELON), 2, 8, false, false, false, 9, 3));
		resources.add(new AnniResource(Material.COAL_ORE, new ItemStack(Material.COAL), 6, 10, true, true, false));
		resources.add(new AnniResource(Material.IRON_ORE, new ItemStack(Material.IRON_ORE), 9, 20, true, false, false));
		resources.add(new AnniResource(Material.GOLD_ORE, new ItemStack(Material.GOLD_ORE), 9, 20, true, false, false));
		resources.add(new AnniResource(Material.REDSTONE_ORE, new ItemStack(Material.REDSTONE_ORE), 9, 20, true, true, false, 9, 4));
		resources.add(new AnniResource(Material.LAPIS_ORE, new ItemStack(Material.INK_SACK, 1, (short) 4), 9, 20, true, true, false, 9, 4));
		resources.add(new AnniResource(Material.DIAMOND_ORE, new ItemStack(Material.DIAMOND), 12, 30, true, true, false));
		resources.add(new AnniResource(Material.EMERALD_ORE, new ItemStack(Material.EMERALD), 18, 40, true, true, false));
	}

	@EventHandler
	public void onBreakResource(BlockBreakEvent event) {
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.ANNI) {
			Player p = event.getPlayer();
			Block b = event.getBlock();
			Location l = b.getLocation();
			if (blockCt.contains(l)) {
				event.setCancelled(true);
				return;
			}
			for (AnniResource resource : resources) {
				if (b.getType().equals(resource.getBlockType())) {
					b.setType(Material.AIR);
					blockCt.add(l);
					Bukkit.getScheduler().runTask(plugin, () -> {
						if (resource.isCobble()) {
							b.setType(Material.COBBLESTONE);
						}
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
						int maxAmount = resource.getMaxAmount();
						int minAmount = resource.getMinAmount();
						int amount = new Random().nextInt(maxAmount - minAmount + 1) + minAmount;
						ItemStack item = resource.getEarnItem();
						item.setAmount(amount);
						ItemStack playerItem = p.getItemInHand();
						if (playerItem != null) {
							if (resource.allowFortune() && playerItem.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
								int level = playerItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
								if (level > 3) {
									level = 3;
								}
								item.setAmount(item.getAmount() + level);
							}
							if (resource.allowSilkTouch() && playerItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
								item.setType(resource.getBlockType());
							}
						}
						p.getInventory().addItem(item);
						p.giveExp(resource.getXp());
					});
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							b.setType(resource.getBlockType());
							blockCt.remove(b.getLocation());
						}
					}, 20 * resource.getCooldown());
					return;
				}
			}
		}
	}

	@EventHandler
	public void onResourcePlace(BlockPlaceEvent event) {
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.ANNI) {
			for (AnniResource resource : resources) {
				if (event.getBlock().getType().equals(resource.getBlockType())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

}
