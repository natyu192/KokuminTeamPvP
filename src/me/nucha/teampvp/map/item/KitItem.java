package me.nucha.teampvp.map.item;

import org.bukkit.configuration.ConfigurationSection;

public class KitItem extends ConfigItem {

	private int slot;

	public KitItem(ConfigurationSection config) {
		super(config);
		this.slot = config.getInt("slot");
	}

	public int getSlot() {
		return slot;
	}

}
