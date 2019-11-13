package me.nucha.teampvp.game;

public enum TeamGameType {
	TDM("Team Deathmatch"), DTM("Destroy the Monument"), CTW("Capture the Wool"), ANNI("Annihilation");
	private String displayName;

	private TeamGameType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
