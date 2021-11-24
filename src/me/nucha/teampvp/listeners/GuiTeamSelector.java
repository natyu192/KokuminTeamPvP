package me.nucha.teampvp.listeners;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.nucha.teampvp.TeamPvP;
import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamManager;
import me.nucha.teampvp.staff.StaffManager;
import me.nucha.teampvp.utils.ColorUtils;
import me.nucha.teampvp.utils.CustomItem;

public class GuiTeamSelector implements Listener {

	private static Inventory gui;
	private static Inventory gui_premium;
	private static HashMap<Integer, PvPTeam> premium_teamslot;
	private static TeamPvP plugin;

	public GuiTeamSelector(TeamPvP plugin) {
		init(plugin);
	}

	public static void init(TeamPvP plugin) {
		GuiTeamSelector.plugin = plugin;
		GuiTeamSelector.premium_teamslot = new HashMap<>();
		GuiTeamSelector.gui = Bukkit.createInventory(null, 9, "§aチーム選択");
		GuiTeamSelector.gui_premium = Bukkit.createInventory(null, 9, "§dチーム選択");
		TeamManager teamManager = plugin.getTeamManager();
		ItemStack random = new CustomItem(Material.WOOL, 1, "§eランダムで参加");
		ItemMeta randomMeta = random.getItemMeta();
		randomMeta.setLore(Arrays.asList(new String[] { "§e" + teamManager.total() + "§7人が参加しています" }));
		random.setItemMeta(randomMeta);
		ItemStack quit = new CustomItem(Material.IRON_DOOR, 1, "§6チームから抜ける");
		gui.setItem(0, random);
		gui.setItem(8, quit);
		gui_premium.setItem(0, random);
		gui_premium.setItem(8, quit);
		int slot = 1;
		for (PvPTeam team : teamManager.getTeams()) {
			ItemStack item = new CustomItem(Material.WOOL, 1, team.getDisplayName() + "§eに参加",
					ColorUtils.CHAT_TO_DYE(team.getColor()).getWoolData());
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setLore(Arrays.asList(new String[] { "§e" + team.size() + "/" + team.getMax() + "§7人が参加しています" }));
			item.setItemMeta(itemMeta);
			gui_premium.setItem(slot, item);
			premium_teamslot.put(slot, team);
			slot++;
		}
	}

	public static void open(Player p) {
		if (p.hasPermission("kokuminteampvp.premium")) {
			p.openInventory(gui_premium);
		} else {
			p.openInventory(gui);
		}
	}

	public static void update() {
		ItemStack random = gui.getItem(0);
		ItemMeta randomMeta = random.getItemMeta();
		TeamManager teamManager = plugin.getTeamManager();
		int total = teamManager.total();
		randomMeta.setLore(Arrays.asList(new String[] { "§e" + total + "§7人が参加しています" }));
		random.setItemMeta(randomMeta);
		random = gui_premium.getItem(0);
		randomMeta = random.getItemMeta();
		randomMeta.setLore(Arrays.asList(new String[] { "§e" + total + "§7人が参加しています" }));
		random.setItemMeta(randomMeta);
		int i = 1;
		for (PvPTeam team : teamManager.getTeams()) {
			ItemStack item = gui_premium.getItem(i);
			ItemMeta itemMeta = item.getItemMeta();
			String color = "§e";
			int size = team.size();
			int max = team.getMax();
			if (size >= max) {
				color = "§c";
			}
			itemMeta.setLore(Arrays.asList(new String[] {
					color + size + "/" + max + "§7人が参加しています" }));
			item.setItemMeta(itemMeta);
			i++;
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (p.getOpenInventory().getTopInventory() != null) {
			if (p.getOpenInventory().getTopInventory().getName().startsWith("§aチーム選択")) {
				event.setCancelled(true);
				if (event.getSlotType().equals(SlotType.OUTSIDE) || event.getCurrentItem().getType().equals(Material.AIR)) {
					return;
				}
				if (event.getClickedInventory().equals(p.getOpenInventory().getTopInventory())) {
					ItemStack item = event.getCurrentItem();
					if (item == null)
						return;
					int slot = event.getSlot();
					if ((slot != 8) && StaffManager.isStaffMode(p)) {
						p.sendMessage("§c管理者モードがオンになっているため、参加することができません");
						return;
					}
					if (slot == 0) {
						if (plugin.getTeamManager().getGamePlayers().contains(p)) {
							p.sendMessage("§c既に参加しています");
							return;
						}
						plugin.getTeamManager().joinRandom(p);
						p.sendMessage(plugin.getTeamManager().getTeam(p).getDisplayName() + "に参加しました");
						p.closeInventory();
						update();
					}
					if (slot == 8) {
						plugin.getTeamManager().leaveTeam(p);
						p.sendMessage("§6チームから抜けました");
						p.closeInventory();
						update();
					}
				}
			}
			if (p.getOpenInventory().getTopInventory().getName().startsWith("§dチーム選択")) {
				event.setCancelled(true);
				if (event.getSlotType().equals(SlotType.OUTSIDE) || event.getCurrentItem().getType().equals(Material.AIR)) {
					return;
				}
				if (event.getClickedInventory().equals(p.getOpenInventory().getTopInventory())) {
					ItemStack item = event.getCurrentItem();
					if (item == null)
						return;
					int slot = event.getSlot();
					if (slot == 0) {
						if (plugin.getTeamManager().getGamePlayers().contains(p)) {
							p.sendMessage("§c既に参加しています");
							return;
						}
						plugin.getTeamManager().joinRandom(p);
						p.sendMessage(plugin.getTeamManager().getTeam(p).getDisplayName() + "に参加しました");
						p.closeInventory();
						update();
					} else if (slot == 8) {
						plugin.getTeamManager().leaveTeam(p);
						p.sendMessage("§6チームから抜けました");
						p.closeInventory();
						update();
					} else {
						if (plugin.getTeamManager().getGamePlayers().contains(p)) {
							p.sendMessage("§c既に参加しています");
							return;
						}
						plugin.getTeamManager().joinTeam(p, premium_teamslot.get(slot));
						p.sendMessage(plugin.getTeamManager().getTeam(p).getDisplayName() + "に参加しました");
						p.closeInventory();
						update();
					}
				}
			}
		}
	}

}
