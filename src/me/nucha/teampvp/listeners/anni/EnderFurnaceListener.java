package me.nucha.teampvp.listeners.anni;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.map.config.AnniConfig;
import net.minecraft.server.v1_8_R3.TileEntityFurnace;

public class EnderFurnaceListener implements Listener {

	private TeamPvP plugin;
	private HashMap<String, CraftInventoryFurnace> furnaces;
	private List<Location> furnaceLocations;

	public EnderFurnaceListener(TeamPvP plugin) {
		this.plugin = plugin;
		this.furnaces = new HashMap<>();
		AnniConfig config = (AnniConfig) plugin.getMapManager().getCurrentMapInfo().getMapConfig();
		this.furnaceLocations = config.getEnderFurnaces();
	}

	@EventHandler
	public void onOpenFurnace(PlayerInteractEvent event) {
		if (plugin.getGameManager().getTeamGameType() == TeamGameType.ANNI && MatchState.isState(MatchState.IN_GAME)) {
			if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			Player p = event.getPlayer();
			Block b = event.getClickedBlock();

			if (b.getType().equals(Material.FURNACE) || b.getType().equals(Material.BURNING_FURNACE)) {
				if (furnaceLocations.contains(b.getLocation())) {
					event.setCancelled(true);
					p.sendMessage("§5それはプライベートかまどです。自分専用のかまどに繋がっています。");
					openFurnace(p);
				}
				/*Location bl = b.getLocation();
				for (Location efl : config.getEnderFurnaces()) {
				if (bl.getX() == efl.getX() && bl.getY() == efl.getY() && bl.getZ() == efl.getZ()) {
					if (config.getEnderFurnaces().contains(b.getLocation())) {
						event.setCancelled(true);
						p.sendMessage("§5それはプライベートかまどです。自分専用のかまどに繋がっています。");
						openFurnace(p);
					}
				}
				}*/
			}
		}
	}

	private void registerFurnace(Player p) {
		if (!furnaces.containsKey(p.getName())) {
			int x = 10000000 + furnaces.size();
			int y = 255;
			int z = 10000000;
			p.getWorld().getBlockAt(x, y, z).setType(Material.FURNACE);
			TileEntityFurnace furnace = ((TileEntityFurnace) ((CraftWorld) p.getWorld())
					.getTileEntityAt(x, y, z));
			furnaces.put(p.getName(), new CraftInventoryFurnace(furnace));
			// p.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
		}
	}

	private void openFurnace(Player p) {
		registerFurnace(p);
		p.openInventory(furnaces.get(p.getName()));
	}

}
