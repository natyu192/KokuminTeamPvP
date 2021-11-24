package me.nucha.teampvp.listeners.anni;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AnniResource {

	private Material blockType;
	private ItemStack earnItem;
	private int xp;
	private int cooldown;
	private boolean cobble;
	private boolean fortune;
	private boolean silktouch;
	private int maxAmount;
	private int minAmount;

	public AnniResource(Material blockType, ItemStack earnItem, int xp,
			int cooldown, boolean cobble, boolean fortune, boolean silktouch) {
		this(blockType, earnItem, xp, cooldown, cobble, fortune, silktouch, 1, 1);
	}

	public AnniResource(Material blockType, ItemStack earnItem, int xp,
			int cooldown, boolean cobble, boolean fortune, boolean silktouch, int maxAmount, int minAmount) {
		this.blockType = blockType;
		earnItem.setAmount(1);
		this.earnItem = earnItem;
		this.xp = xp;
		this.cooldown = cooldown;
		this.cobble = cobble;
		this.fortune = fortune;
		this.silktouch = silktouch;
		this.maxAmount = maxAmount;
		this.minAmount = minAmount;
	}

	public int getXp() {
		return xp;
	}

	public int getCooldown() {
		return cooldown;
	}

	public boolean isCobble() {
		return cobble;
	}

	public boolean allowFortune() {
		return fortune;
	}

	public boolean allowSilkTouch() {
		return silktouch;
	}

	public Material getBlockType() {
		return blockType;
	}

	public ItemStack getEarnItem() {
		return earnItem;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

}
