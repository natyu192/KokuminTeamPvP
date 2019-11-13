package me.nucha.teampvp.game.tnt;

import java.util.Random;

import net.minecraft.server.v1_8_R3.EntityTNTPrimed;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TNTInfo {

	private Player owner;
	private Block tnt;
	private EntityTNTPrimed primed;
	private boolean hasPrimed;
	private int id;

	public TNTInfo(Player owner, Block tnt) {
		this.owner = owner;
		this.tnt = tnt;
		this.hasPrimed = false;
		this.id = new Random().nextInt(9999);
	}

	public Player getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public Block getTnt() {
		return tnt;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public void prime(EntityTNTPrimed primed) {
		this.primed = primed;
		this.hasPrimed = true;
	}

	public boolean hasPrimed() {
		return hasPrimed;
	}

	public EntityTNTPrimed getPrimedTnt() {
		return primed;
	}

	public int getId() {
		return id;
	}

}
