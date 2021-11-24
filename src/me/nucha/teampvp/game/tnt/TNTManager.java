package me.nucha.teampvp.game.tnt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.nucha.teampvp.TeamPvP;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class TNTManager {

	private List<TNTInfo> tntInfos = new ArrayList<>();
	private HashMap<Block, Player> redstoneMap = new HashMap<>();
	private TeamPvP plugin;

	public TNTManager(TeamPvP plugin) {
		this.plugin = plugin;
	}

	public void registerInfo(TNTInfo tntInfo) {
		tntInfos.add(tntInfo);
	}

	public TNTInfo createInfo(Player owner, Block tnt) {
		TNTInfo tntInfo = new TNTInfo(owner, tnt);
		tntInfos.add(tntInfo);
		return tntInfo;
	}

	public void unregisterInfo(int id) {
		if (getInfoById(id) != null) {
			tntInfos.remove(getInfoById(id));
		}
	}

	public void unregisterInfo(Block block) {
		if (getInfoByBlock(block) != null) {
			tntInfos.remove(getInfoByBlock(block));
		}
	}

	public TNTInfo getInfoById(int id) {
		for (TNTInfo tntInfo : tntInfos) {
			if (tntInfo.getId() == id) {
				return tntInfo;
			}
		}
		return null;
	}

	public TNTInfo getInfoByBlock(Block block) {
		for (TNTInfo tntInfo : tntInfos) {
			if (tntInfo.getTnt().equals(block)) {
				return tntInfo;
			}
		}
		return null;
	}

	public TNTInfo getInfoByPrimedTNT(TNTPrimed tntPrimed) {
		for (TNTInfo tntInfo : tntInfos) {
			if (!tntInfo.hasPrimed())
				continue;
			if (tntInfo.getPrimedTnt().getBukkitEntity().getEntityId() == tntPrimed.getEntityId()) {
				return tntInfo;
			}
		}
		return null;
	}

	public HashMap<Block, Player> getRedstoneMap() {
		return redstoneMap;
	}

	public void putRedstone(Block b, Player p) {
		redstoneMap.put(b, p);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			redstoneMap.remove(b);
		}, 30L);
	}

}
