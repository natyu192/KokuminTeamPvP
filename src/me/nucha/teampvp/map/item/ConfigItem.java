package me.nucha.teampvp.map.item;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.utils.ColorUtils;
import me.nucha.teampvp.utils.SoulBound;

public class ConfigItem {

	private Material material;
	private ItemStack itemStack;
	private ItemMeta itemMeta;
	private int amount;
	private short damage;
	private boolean soulbound;
	private boolean unbreakable;

	public ConfigItem(ConfigurationSection config) {
		String path = config.getCurrentPath();
		TeamPvP.sendConsoleMessage("§e§lconfig.ymlからアイテムを読み込みます: " + path);
		this.material = Material.valueOf(config.getString("material").toUpperCase());
		if (config.isSet("amount")) {
			this.amount = config.getInt("amount");
		} else {
			this.amount = 1;
		}
		if (config.isSet("damage")) {
			this.damage = (short) config.getInt("damage");
		} else {
			this.damage = 0;
		}
		this.itemStack = new ItemStack(material, amount, damage);
		if (config.isSet("soulbound")) {
			boolean soulbound = config.getBoolean("soulbound");
			if (soulbound) {
				SoulBound.setSoulBound(itemStack, true);
			}
			this.soulbound = soulbound;
		} else {
			this.soulbound = false;
		}
		this.itemMeta = itemStack.getItemMeta();
		if (config.isSet("displayName"))
			this.itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("displayName")));
		if (config.isSet("lore")) {
			List<String> lores = new ArrayList<>();
			for (String lore : config.getStringList("lore")) {
				lores.add(ChatColor.translateAlternateColorCodes('&', lore));
			}
			this.itemMeta.setLore(lores);
		}
		this.itemStack.setItemMeta(itemMeta);
		if (config.isSet("leather_color")) {
			Color color = ColorUtils.COLORSTR_TO_COLORRGB(config.getString("leather_color"));
			if (color == null) {
				TeamPvP.sendConsoleMessage("§d" + material.toString() + ": color null");
			}
			LeatherArmorMeta lam = (LeatherArmorMeta) this.itemMeta;
			lam.setColor(color);
			this.itemStack.setItemMeta(lam);
		}
		if (config.isSet("color")) {
			DyeColor color = DyeColor.valueOf(config.getString("color").toUpperCase());
			if (color == null) {
				TeamPvP.sendConsoleMessage("§d" + material.toString() + ": color null");
			}
			if (material == Material.WOOL) {
				this.damage = color.getData();
			} else if (material == Material.STAINED_GLASS ||
					material == Material.STAINED_GLASS_PANE ||
					material == Material.STAINED_CLAY) {
				this.damage = color.getData();
			}
			this.itemStack.setDurability(damage);
		}
		if (config.isSet("unbreakable")) {
			boolean unbreakable = config.getBoolean("unbreakable");
			itemMeta.spigot().setUnbreakable(unbreakable);
			this.unbreakable = unbreakable;
		} else {
			this.unbreakable = false;
		}
		if (config.isSet("enchantments")) {
			for (String s : config.getStringList("enchantments")) {
				String[] enchantment = s.split(":");
				Enchantment enc = Enchantment.getByName(enchantment[0]);
				if (enc != null && StringUtils.isNumeric(enchantment[1])) {
					int level = Integer.valueOf(enchantment[1]);
					if (level > 0) {
						itemStack.addUnsafeEnchantment(enc, level);
					}
				}
			}
		}
	}

	public int getAmount() {
		return amount;
	}

	public short getDamage() {
		return damage;
	}

	public ItemMeta getItemMeta() {
		return itemMeta;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public Material getMaterial() {
		return material;
	}

	public boolean isSoulBound() {
		return soulbound;
	}

	public boolean isUnbreakable() {
		return unbreakable;
	}

}
