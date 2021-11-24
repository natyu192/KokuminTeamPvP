package me.nucha.teampvp.staff;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nucha.core.packet.PacketHandler;
import me.nucha.core.packet.PacketInfo;
import me.nucha.core.packet.PacketListener;
import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.utils.CustomItem;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;

public class StaffManager {

	private static TeamPvP plugin;
	private static Set<UUID> staffMode;
	private static PacketListener vanishListener;

	private static ItemStack item_teleport;
	private static ItemStack item_wewand;

	public static void init(TeamPvP plugin) {
		StaffManager.plugin = plugin;
		staffMode = new HashSet<>();
		PacketHandler.registerPacketListener(vanishListener = new PacketListener() {
			@Override
			public void playerReceivePacket(PacketInfo packet) {
				if (packet.getPacket() instanceof PacketPlayOutNamedEntitySpawn) {
					UUID id = (UUID) packet.getPacketValue("b");
					Player p = packet.getPlayer();
					if (isStaffMode(id) && !p.hasPermission("kokuminteampvp.staff")) {
						packet.setCancelled(true);
					}
				}
				if (packet.getPacket() instanceof PacketPlayOutPlayerInfo) {
					EnumPlayerInfoAction a = (EnumPlayerInfoAction) packet.getPacketValue("a");
					if (a.equals(EnumPlayerInfoAction.ADD_PLAYER)) {
						Player p = packet.getPlayer();
						@SuppressWarnings("unchecked")
						List<PlayerInfoData> b = (List<PlayerInfoData>) packet.getPacketValue("b");
						for (PlayerInfoData data : b) {
							if (isStaffMode(data.a().getId()) && !p.hasPermission("kokuminteampvp.staff")) {
								packet.setCancelled(true);
							}
						}
					}
				}
			}

			@Override
			public void playerSendPacket(PacketInfo packet) {
			}
		});

		item_teleport = new CustomItem(Material.COMPASS, 1, "§cテレポートコンパス", "§7左クリックすると", "§7目の先にある奥のブロックに", "§7テレポートできます", "",
				"§7右クリックすると", "§7目の前にある壁の奥に", "§7テレポートできます");
		item_wewand = new CustomItem(Material.WOOD_AXE, 1, "§6WorldEdit Wand", "§7WorldEditのWandです");
	}

	public static void shutdown() {
		PacketHandler.unregisterPacketListener(vanishListener);
	}

	public static boolean isStaffMode(UUID uuid) {
		return staffMode.contains(uuid);
	}

	public static boolean isStaffMode(Player p) {
		return staffMode.contains(p.getUniqueId());
	}

	public static void setStaffMode(Player p, boolean staff) {
		if (!plugin.getTeamManager().getSpectators().contains(p)) {
			return;
		}
		if (staff) {
			staffMode.add(p.getUniqueId());
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (!all.hasPermission("kokuminteampvp.staff")) {
					all.hidePlayer(p);
				}
			}
			setStaffItem(p);
			p.sendMessage("§d管理者モードがオンになりました:");
			p.sendMessage("§8- §a一般プレイヤーから見えなくなりました(Vanish)");
			p.sendMessage("§8- §aチェストを開くことができるようになりました");
			p.sendMessage("§8- §cゲームへ参加できなくなりました");
		} else {
			staffMode.remove(p.getUniqueId());
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (!MatchState.isState(MatchState.IN_GAME) || plugin.getTeamManager().getSpectators().contains(all)) {
					all.showPlayer(p);
				}
			}
			plugin.getKitManager().setSpectatorItem(p);
			p.sendMessage("§d管理者モードがオフになりました:");
			p.sendMessage("§8- §c一般プレイヤーから見えるようになりました");
			p.sendMessage("§8- §cチェストを開けなくなりました");
			p.sendMessage("§8- §aゲームへ参加できるようになりました");
		}
	}

	public static void setStaffItem(Player p) {
		p.getInventory().clear();
		ItemStack air = new ItemStack(Material.AIR);
		p.getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
		p.getInventory().setItem(0, item_teleport);
		p.getInventory().setItem(1, item_wewand);
		p.getInventory().setHeldItemSlot(1);
		p.updateInventory();
	}

}
