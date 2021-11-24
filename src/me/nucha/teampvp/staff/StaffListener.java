package me.nucha.teampvp.staff;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

public class StaffListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && StaffManager.isStaffMode(p)) {
			Block b = event.getClickedBlock();
			if (b.getState() instanceof InventoryHolder) {
				event.setCancelled(true);
				p.openInventory(((InventoryHolder) b.getState()).getInventory());
				p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1, 0.5f);
			}
		}
	}

}
