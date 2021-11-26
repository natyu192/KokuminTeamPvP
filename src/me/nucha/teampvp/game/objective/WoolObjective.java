package me.nucha.teampvp.game.objective;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.utils.ScoreboardUtils;
import me.nucha.teampvp.utils.SymbolUtils;

public class WoolObjective extends GameObjective {

	private Location location;
	private DyeColor color;
	private List<String> pickers;
	private List<String> pickersAtLeastOnce;
	private boolean placed;

	public WoolObjective(String displayName, TeamGameType teamGameType, PvPTeam ownTeam, Location location, DyeColor color) {
		super(displayName, teamGameType, ownTeam);
		this.location = location;
		this.color = color;
		this.pickers = new ArrayList<>();
		this.pickersAtLeastOnce = new ArrayList<>();
		this.placed = false;
	}

	public ChatColor dyeColorToChatColor(DyeColor color) {
		switch (color) {
		case BLACK:
			return ChatColor.BLACK;
		case BLUE:
			return ChatColor.DARK_BLUE;
		case CYAN:
			return ChatColor.DARK_AQUA;
		case GRAY:
			return ChatColor.DARK_GRAY;
		case GREEN:
			return ChatColor.DARK_GREEN;
		case LIME:
			return ChatColor.GREEN;
		case LIGHT_BLUE:
			return ChatColor.BLUE;
		case MAGENTA:
			return ChatColor.LIGHT_PURPLE;
		case ORANGE:
			return ChatColor.GOLD;
		case PINK:
			return ChatColor.RED;
		case PURPLE:
			return ChatColor.DARK_PURPLE;
		case RED:
			return ChatColor.DARK_RED;
		case SILVER:
			return ChatColor.GRAY;
		case WHITE:
			return ChatColor.WHITE;
		case YELLOW:
			return ChatColor.YELLOW;
		default:
			return null;
		}
	}

	public List<String> getPickers() {
		return pickers;
	}

	public boolean hasPickedAtLeastOnce(Player p) {
		return pickersAtLeastOnce.contains(p.getName());
	}

	public boolean hasPicked(Player p) {
		return pickers.contains(p.getName());
	}

	public void picked(Player p) {
		if (!hasPicked(p)) {
			pickers.add(p.getName());
			if (!hasPickedAtLeastOnce(p)) {
				pickersAtLeastOnce.add(p.getName());
			}
		}
	}

	public boolean doesSomeoneHasWool() {
		return pickers.size() > 0;
	}

	public void lost(Player p) {
		pickers.remove(p.getName());
	}

	public boolean isPlaced() {
		return placed;
	}

	public void place() {
		placed = true;
	}

	public String getWoolName() {
		String wooldef = color.name().toUpperCase().replaceAll("_", " ");
		if (wooldef.contains(" ")) {
			String[] words = wooldef.split(" ");
			boolean first = true;
			String theName = null;
			for (String word : words) {
				String word1 = word.substring(0, 1);
				String word2 = word.substring(1).toLowerCase();
				String woolName = word1 + word2;
				if (first) {
					theName = woolName;
					first = false;
				} else {
					theName += " " + woolName;
				}
			}
			theName += " Wool";
			return theName;
		}
		String wool1 = wooldef.substring(0, 1);
		String wool2 = wooldef.substring(1).toLowerCase();
		String woolName = wool1 + wool2 + " Wool";
		return woolName;
	}

	@Override
	public void updateStateOnScoreboard() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			String localTeamName = getWoolName();
			// TODO add dummy colorcode
			Team localTeam = ScoreboardUtils.getOrCreateTeam(all, localTeamName);
			switch (getState()) {
			case IN_COPLETE:
				localTeam.setPrefix(" " + dyeColorToChatColor(color) + SymbolUtils.wool_unpicked() + " §r");
				break;
			case SEMI_COMPLETED:
				localTeam.setPrefix(" " + dyeColorToChatColor(color) + SymbolUtils.wool_pickedup() + " §r");
				break;
			case COPLETED:
				localTeam.setPrefix(" " + dyeColorToChatColor(color) + SymbolUtils.wool_placed() + " §r");
				break;
			}
		}
	}

	@Override
	public void displayOnScoreboard(Player p, int score) {
		String localTeamName = getWoolName();
		// TODO add dummy colorcode
		ScoreboardUtils.replaceScore(p, score, "", localTeamName, "");
		Team localTeam = ScoreboardUtils.getOrCreateTeam(p, localTeamName);
		switch (getState()) {
		case IN_COPLETE:
			localTeam.setPrefix(" " + dyeColorToChatColor(color) + SymbolUtils.wool_unpicked() + " §r");
			break;
		case SEMI_COMPLETED:
			localTeam.setPrefix(" " + dyeColorToChatColor(color) + SymbolUtils.wool_pickedup() + " §r");
			break;
		case COPLETED:
			localTeam.setPrefix(" " + dyeColorToChatColor(color) + SymbolUtils.wool_placed() + " §r");
			break;
		}
	}

	@Override
	public String getText() {
		String prefix = null;
		switch (getState()) {
		case IN_COPLETE:
			prefix = " " + dyeColorToChatColor(color) + SymbolUtils.wool_unpicked() + " §r";
			break;
		case SEMI_COMPLETED:
			prefix = " " + dyeColorToChatColor(color) + SymbolUtils.wool_pickedup() + " §r";
			break;
		case COPLETED:
			prefix = " " + dyeColorToChatColor(color) + SymbolUtils.wool_placed() + " §r";
			break;
		}
		return prefix + getWoolName();
	}

	public DyeColor getColor() {
		return color;
	}

	public ChatColor getChatColor() {
		return dyeColorToChatColor(color);
	}

	public Location getLocation() {
		return location;
	}

}
