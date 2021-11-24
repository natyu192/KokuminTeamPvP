package me.nucha.teampvp.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.game.stats.StatsCategory;
import me.nucha.teampvp.game.stats.StatsManager;
import me.nucha.teampvp.map.item.ConfigItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class KillListener implements Listener {

	private TeamPvP plugin;
	// private List<Player> respawning;

	public KillListener(TeamPvP plugin) {
		this.plugin = plugin;
		// this.respawning = new ArrayList<>();
	}

	/*@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (MatchState.isState(MatchState.IN_GAME) && event.getCause() == DamageCause.VOID && !respawning.contains(p)) {
				event.setDamage(40);
			}
			if (p.getHealth() <= event.getFinalDamage()) {
				event.setDamage(0);
				List<ItemStack> drops = new ArrayList<>();
				for (ItemStack content : p.getInventory().getContents())
					drops.add(content);
				for (ItemStack armor : p.getInventory().getArmorContents())
					drops.add(armor);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerDeathEvent(p, drops, 0, null));
				p.getInventory().clear();
				ItemStack air = new ItemStack(Material.AIR);
				p.getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
				p.setFlying(true);
				p.setHealth(20);
				p.setSaturation(3);
				p.setFoodLevel(20);
				p.setFireTicks(0);
				respawning.add(p);
				p.playSound(p.getLocation(), Sound.IRONGOLEM_DEATH, 1f, 1f);
				BukkitRunnable task = new BukkitRunnable() {
					double respawnTimeMax = 3;
					double respawnTime = respawnTimeMax;
	
					@Override
					public void run() {
						if (!MatchState.isState(MatchState.IN_GAME) || p == null || !p.isOnline()) {
							respawning.remove(p);
							cancel();
							return;
						}
						if (respawnTime == respawnTimeMax) {
							PotionEffect effectBlind = new PotionEffect(PotionEffectType.BLINDNESS, (int) respawnTimeMax * 20, 0);
							p.addPotionEffect(effectBlind);
							TitleUtil.sendTitle(p, "§c死んでしまった！", EnumTitleAction.TITLE, 2, (int) respawnTimeMax * 20, 2);
						}
						if (respawnTime == 0) {
							TitleUtil.sendTitle(p, "§aリスポーンしました", EnumTitleAction.TITLE, 2, 40, 2);
							respawning.remove(p);
							for (PotionEffect effect : p.getActivePotionEffects()) {
								p.removePotionEffect(effect.getType());
							}
							KitManager kitManager = plugin.getKitManager();
							TeamManager teamManager = plugin.getTeamManager();
							PvPTeam team = teamManager.getTeam(p);
							if (team == teamManager.getSpectatorTeam() || !(MatchState.isState(MatchState.IN_GAME))) {
								p.teleport(kitManager.getSpectatorSpawn());
							} else {
								p.teleport(kitManager.getSpawn(team));
							}
							GameManager gameManager = plugin.getGameManager();
							gameManager.giveKit(p);
							cancel();
							return;
						}
						if (respawnTime > 0) {
							double formattedTime = new BigDecimal(respawnTime).setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
							TitleUtil.sendTitle(p, "§aリスポーンまで §e" + formattedTime + " §a秒...", EnumTitleAction.SUBTITLE);
							respawnTime -= 0.1;
						}
					}
				};
				task.runTaskTimer(plugin, 2L, 2L);
				p.teleport(p.getLocation().add(0, 3, 0));
			}
		}
	}*/

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player p = event.getEntity();
		BukkitRunnable respawnTask = new BukkitRunnable() {
			@Override
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
				for (ChatColor color : ChatColor.values()) {
					char colorChar = color.getChar();
					deathMessageClone = deathMessageClone.replaceAll("§" + colorChar, "§" + colorChar + "§l");
				}
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
					TeamPvP.addCoin(k, 50, TeamGameType.TDM.getDisplayName() + "ボーナス");
				} else {
					TeamPvP.addCoin(k, 30);
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

	public static void respawn(Player p) {
		((CraftPlayer) p).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
	}

}
