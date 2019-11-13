package me.nucha.teampvp.game.objective;

import java.util.ArrayList;
import java.util.List;

import me.nucha.teampvp.game.PvPTeam;

public class GameObjectiveManager {

	private List<GameObjective> objectives;

	public GameObjectiveManager() {
		this.objectives = new ArrayList<>();
	}

	public void registerObjective(GameObjective obj) {
		objectives.add(obj);
	}

	public void unregisterObjective(GameObjective obj) {
		objectives.remove(obj);
	}

	public List<GameObjective> getObjectives() {
		return objectives;
	}

	public GameObjective getObjective(String name, PvPTeam ownTeam) {
		for (GameObjective obj : objectives) {
			if (obj.getDisplayName().equalsIgnoreCase(name) && obj.getOwnTeam() == ownTeam) {
				return obj;
			}
		}
		return null;
	}

}
