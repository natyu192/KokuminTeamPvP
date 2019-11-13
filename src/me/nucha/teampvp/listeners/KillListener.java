package me.nucha.teampvp.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.kokumin.coin.Coin;
import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.stats.StatsCategory;
import me.nucha.teampvp.game.stats.StatsManager;
import me.nucha.teampvp.map.item.ConfigItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class KillListener implements Listener {

	private TeamPvP plugin;

	public KillListener(TeamPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getEntity();
		BukkitRunnable respawnTask = new BukkitRunnable() {
			public void run() {
				if (p != null && p.isOnline() && p.isDead()) {
					respawn(p);
				}
			};
		};
		respawnTask.runTaskLater(plugin, 20 * 3);
		String pn = plugin.getTeamManager().getTeam(p).getColor() + p.getName();
		String deathMessage = null;
		if (p.getKiller() == null) {
			if (p.getLastDamageCause() != null) {
				switch (p.getLastDamageCause().getCause()) {
				case BLOCK_EXPLOSION:
					deathMessage = p.getDisplayName() + "§7は爆発に巻き込まれた";
					break;
				case DROWNING:
					deathMessage = p.getDisplayName() + "§7は溺れた";
					break;
				case ENTITY_EXPLOSION:
					deathMessage = p.getDisplayName() + "§7は爆発した";
					break;
				case FALL:
					deathMessage = p.getDisplayName() + "§7は落下死した (" + (int) p.getFallDistance() + " blocks)";
					break;
				case FALLING_BLOCK:
					deathMessage = p.getDisplayName() + "§7は落ちてきたブロックに押しつぶされた";
					break;
				case FIRE:
					deathMessage = p.getDisplayName() + "§7はこんがりと焼けた";
					break;
				case FIRE_TICK:
					deathMessage = p.getDisplayName() + "§7は燃え尽きた";
					break;
				case LAVA:
					deathMessage = p.getDisplayName() + "§7は溶岩に落ちた";
					break;
				case LIGHTNING:
					deathMessage = p.getDisplayName() + "§7に雷が落ちた";
					break;
				case MAGIC:
					deathMessage = p.getDisplayName() + "§7は魔法で死んだ";
					break;
				case VOID:
					deathMessage = p.getDisplayName() + "§7は奈落に落ちた";
					break;
				case STARVATION:
					deathMessage = p.getDisplayName() + "§7は餓死した";
					break;
				case SUFFOCATION:
					deathMessage = p.getDisplayName() + "§7は窒息死した";
					break;
				default:
					deathMessage = p.getDisplayName() + "§7は死んだ";
					break;
				}
			}
		} else {
			Player k = p.getKiller();
			String kn = plugin.getTeamManager().getTeam(k).getColor() + k.getName();
			switch (p.getLastDamageCause().getCause()) {
			case BLOCK_EXPLOSION:
				deathMessage = pn + "§7は" + kn + "§7に爆発された";
				break;
			case ENTITY_EXPLOSION:
				deathMessage = pn + "§7は" + kn + "§7に爆発された";
				break;
			case FALL:
				deathMessage = pn + "§7は" + kn + "§7に突き落とされた (" + (int) p.getFallDistance() + " blocks)";
				break;
			case FIRE:
				deathMessage = pn + "§7は" + kn + "§7と戦いながらこんがりと焼けた";
				break;
			case FIRE_TICK:
				deathMessage = pn + "§7は" + kn + "§7と戦いながら燃え尽きた";
				break;
			case LAVA:
				deathMessage = pn + "§7は" + kn + "§7に溶岩に突き落とされた";
				break;
			case MAGIC:
				deathMessage = pn + "§7は" + kn + "§7の魔法で死んだ";
				break;
			case VOID:
				deathMessage = pn + "§7は" + kn + "§7に奈落に突き落とされた";
				break;
			case PROJECTILE:
				deathMessage = pn + "§7は" + kn + "§7に撃たれた (" + (int) p.getLocation().distance(k.getLocation()) + " blocks)";
				break;
			default:
				ItemStack item = k.getItemInHand();
				if (item.getItemMeta() == null) {
					deathMessage = pn + "§7は" + kn + "§7に倒された";
					return;
				}
				String name = item.getItemMeta().getDisplayName();
				if (name != null) {
					deathMessage = pn + "§7は" + kn + "§7の" + name + "§7で倒された";
				} else {
					String type = item.getType().toString();
					type = stringFormat(type);
					if (type.equalsIgnoreCase("AIR")) {
						deathMessage = pn + "§7は" + kn + "§7に倒された";
					} else {
						deathMessage = pn + "§7は" + kn + "§7の" + type + "§7で倒された";
					}
				}
				break;
			}
		}
		TeamPvP.sendConsoleMessage(deathMessage);
		for (Player all : Bukkit.getOnlinePlayers()) {
			String deathMessageClone = deathMessage;
			if (p.getName().equalsIgnoreCase(all.getName())
					|| (p.getKiller() != null && p.getKiller().getName().equalsIgnoreCase(all.getName()))) {
				deathMessageClone = deathMessageClone.replaceAll("§7", "§7§l");
				deathMessageClone = deathMessageClone.replaceAll("§c", "§c§l");
				deathMessageClone = deathMessageClone.replaceAll("§9", "§9§l");
			}
			all.sendMessage(deathMessageClone);
		}
		event.setDeathMessage(null);
		StatsManager statsManager = plugin.getStatsManager();
		statsManager.getStatsInfo(p).add(StatsCategory.DEATHS, 1);
		Player k = p.getKiller();
		if (k != null) {
			statsManager.getStatsInfo(k).add(StatsCategory.KILLS, 1);
			if (TeamPvP.pl_kokuminserver) {
				if (plugin.getGameManager().getTeamGameType() == TeamGameType.TDM) {
					Coin.addCoin(k, 50, TeamGameType.TDM.getDisplayName() + "ボーナス");
				} else {
					Coin.addCoin(k, 30);
				}
			}
			List<ConfigItem> killRewards = plugin.getKitManager().getKillRewards();
			if (killRewards != null && killRewards.size() > 0) {
				for (ConfigItem item : killRewards) {
					k.getInventory().addItem(item.getItemStack());
				}
			}
		}
	}

	public String stringFormat(String str) {
		str = str.replaceAll("_", " ");
		String[] sprittype = str.split(" ");
		String typename = null;
		for (String typen : sprittype) {
			String t1 = typen.substring(0, 1);
			String t2 = typen.substring(1);
			t2 = t2.toLowerCase();
			String newtypen = t1 + t2;
			if (typename != null) {
				typename += " " + newtypen;
			} else {
				typename = newtypen;
			}
		}
		return typename;
	}

	public void respawn(Player p) {
		((CraftPlayer) p).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
	}

}
