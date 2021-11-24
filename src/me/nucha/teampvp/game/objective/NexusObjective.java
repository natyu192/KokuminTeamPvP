package me.nucha.teampvp.game.objective;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import me.nucha.teampvp.game.PvPTeam;
import me.nucha.teampvp.game.TeamGameType;
import me.nucha.teampvp.utils.ScoreboardUtils;

public class NexusObjective extends GameObjective {

	private Location location;
	private int hp;

	public NexusObjective(String displayName, PvPTeam ownTeam, Location location) {
		this(displayName, ownTeam, location, 75);
	}

	public NexusObjective(String displayName, PvPTeam ownTeam, Location location, int hp) {
		super(displayName, TeamGameType.ANNI, ownTeam);
		this.location = location;
		this.hp = hp;
	}

	public void beAttacked() {
		if (hp > 0) {
			hp--;
			updateStateOnScoreboard();
		}
	}

	@Override
	public void updateStateOnScoreboard() {
		String localTeamName = getOwnTeam().getColor() + "Nexus HP: ";
		for (Player all : Bukkit.getOnlinePlayers()) {
			Team localTeam = ScoreboardUtils.getOrCreateTeam(all, localTeamName);
			localTeam.setSuffix("§e" + hp);
		}
	}

	@Override
	public void displayOnScoreboard(Player p, int score) {
		String localTeamName = getOwnTeam().getColor() + "Nexus HP: ";
		ScoreboardUtils.replaceScore(p, score, "", localTeamName, "");
		Team localTeam = ScoreboardUtils.getOrCreateTeam(p, localTeamName);
		localTeam.setSuffix("§e" + hp);
	}

	@Override
	public String getText() {
		return getOwnTeam().getDisplayName() + ": §e" + hp;
	}

	public Location getLocation() {
		return location;
	}

	public int getHp() {
		return hp;
	}

}
