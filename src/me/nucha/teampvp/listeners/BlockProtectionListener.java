package me.nucha.teampvp.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class BlockProtectionListener implements Listener {

	@EventHandler
	public void onHanging(HangingBreakByEntityEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onHanging2(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ITEM_FRAME
				|| event.getRightClicked().getType() == EntityType.PAINTING) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onHanging3(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType() == EntityType.ITEM_FRAME
				|| event.getEntity().getType() == EntityType.PAINTING) {
			event.setCancelled(true);
		}
	}

	/*@EventHandler
	public void onPaintBreak(PaintingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player) {
			Player p = (Player) event.getRemover();
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}*/

	@EventHandler
	public void onArmorStand(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onArmorStand2(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
			event.setCancelled(true);
		}
	}

}
