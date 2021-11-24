package me.nucha.teampvp.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.tnt.TNTInfo;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.Explosion;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.World;

public class TNTListener implements Listener {

	private TeamPvP plugin;

	public TNTListener(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		Block placed = event.getBlock();
		if (placed.getType() == Material.TNT) {
			Player p = event.getPlayer();
			onPlace(((CraftWorld) placed.getWorld()).getHandle(), placed.getLocation(), p);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		Block placed = event.getBlock();
		if (placed.getType() == Material.TNT) {
			TNTInfo tntInfo = plugin.getTntManager().getInfoByBlock(placed);
			plugin.getTntManager().unregisterInfo(tntInfo.getId());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = event.getPlayer();
			Block b = event.getClickedBlock();
			if (b.getType() == Material.TNT) {
				if (p.getItemInHand() == null)
					return;
				World world = ((CraftWorld) p.getWorld()).getHandle();
				interact(world, b.getLocation(), p, event);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity() instanceof TNTPrimed) {
			TNTPrimed tnt = (TNTPrimed) event.getEntity();
			TNTInfo tntInfo = plugin.getTntManager().getInfoByPrimedTNT(tnt);
			if (tntInfo == null) {
				TeamPvP.sendConsoleMessage("TNTInfo null");
				return;
			}
			List<Block> blockList = new ArrayList<Block>();
			for (Block b : event.blockList()) {
				blockList.add(b);
			}
			if (tntInfo.hasOwner()) {
				Player p = tntInfo.getOwner();
				for (Block b : blockList) {
					if (b.getType() == Material.TNT) {
						event.blockList().remove(b);
						World world = ((CraftWorld) p.getWorld()).getHandle();
						Entity e = event.getEntity();
						double x = e.getLocation().getX();
						double y = e.getLocation().getY();
						double z = e.getLocation().getZ();
						Explosion explosion = new Explosion(world, ((CraftEntity) e).getHandle(), x, y, z, event.getYield(), true, true);
						wasExploded(world, b.getLocation(), explosion, p);
					}
					BlockBreakEvent blockBreakEvent = new BlockBreakEvent(b, p);
					Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
					if (blockBreakEvent.isCancelled()) {
						event.blockList().remove(b);
					}
				}
				plugin.getTntManager().unregisterInfo(tntInfo.getId());
			}
		}
	}

	@EventHandler
	public void onPhysics(BlockPhysicsEvent event) {
		Block b = event.getBlock();
		if (b.getType() == Material.TNT) {
			TNTInfo tntInfo = plugin.getTntManager().getInfoByBlock(b);
			if (tntInfo == null) {
				TeamPvP.sendConsoleMessage("tntInfo == null on doPhysics");
				return;
			}
			event.setCancelled(true);// disable original TNT
			Player p = tntInfo.getOwner();
			doPhysics(((CraftWorld) b.getWorld()).getHandle(), b.getLocation(), p);
		}
	}

	private EntityTNTPrimed primeTnt(World world, Location location, Player source) {
		EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (float) location.getX() + 0.5F, location.getY(),
				(float) location.getZ() + 0.5F, ((CraftPlayer) source).getHandle());
		location.getBlock().setType(Material.AIR);
		world.addEntity(entitytntprimed);
		world.makeSound(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
		TNTInfo tntInfo = plugin.getTntManager().getInfoByBlock(location.getBlock());
		tntInfo.setOwner(source);
		tntInfo.prime(entitytntprimed);
		// TNTManager tntManager = plugin.getTntManager();
		// tntManager.registerInfo(tntInfo);
		return entitytntprimed;
	}

	private void interact(World world, Location location, Player p, PlayerInteractEvent event) {
		EntityHuman entityhuman = ((CraftPlayer) p).getHandle();
		if (entityhuman.bZ() != null) {
			Item item = entityhuman.bZ().getItem();
			if (item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE) {
				event.setCancelled(true);
				Block b = world.getWorld().getBlockAt(location);
				plugin.getTntManager().unregisterInfo(plugin.getTntManager().getInfoByBlock(b).getId());
				TNTInfo tntInfo = new TNTInfo(p, b);
				plugin.getTntManager().registerInfo(tntInfo);
				primeTnt(world, location, p);
				b.setType(Material.AIR);
				if (item == Items.FLINT_AND_STEEL)
					entityhuman.bZ().damage(1, entityhuman);
				else if (!entityhuman.abilities.canInstantlyBuild)
					entityhuman.bZ().count--;
				return;
			}
		}
	}

	private void wasExploded(World world, Location loc, Explosion explosion, Player p) {
		EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (float) loc.getX() + 0.5F, loc.getY(), (float) loc.getZ() + 0.5F,
				explosion.getSource());
		entitytntprimed.fuseTicks = world.random.nextInt(entitytntprimed.fuseTicks / 4) + entitytntprimed.fuseTicks / 8;
		world.addEntity(entitytntprimed);
		TNTInfo tntInfo = new TNTInfo(p, loc.getBlock());
		tntInfo.prime(entitytntprimed);
		loc.getBlock().setType(Material.AIR);
		plugin.getTntManager().registerInfo(tntInfo);
	}

	private void onPlace(World world, Location loc, Player p) {
		BlockPosition blockposition = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
		TNTInfo tntInfo = new TNTInfo(p, loc.getBlock());
		plugin.getTntManager().registerInfo(tntInfo);
		if (world.isBlockIndirectlyPowered(blockposition)) {
			primeTnt(world, loc, p);
			world.setAir(blockposition);
		}
	}

	private void doPhysics(World world, Location loc, Player p) {
		BlockPosition blockposition = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
		TNTInfo tntInfo = new TNTInfo(p, loc.getBlock());
		plugin.getTntManager().registerInfo(tntInfo);
		if (world.isBlockIndirectlyPowered(blockposition)) {
			primeTnt(world, loc, p);
			world.setAir(blockposition);
		}
	}
}
