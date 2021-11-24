package me.nucha.teampvp.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.nucha.teampvp.TeamPvP;

public class UUIDUtils {

	private static FileConfiguration uuidYml;
	private static File uuidFile;

	public static void init(TeamPvP plugin) {
		uuidFile = new File(plugin.getDataFolder(), "uuid.yml");
		if (!uuidFile.exists()) {
			try {
				uuidFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		uuidYml = YamlConfiguration.loadConfiguration(uuidFile);
	}

	public static void shutdown() {
		try {
			uuidYml.save(uuidFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getName(UUID uuid) {
		return getName(uuid.toString());
	}

	public static String getName(String uuid) {
		if (uuidYml.isSet(uuid)) {
			return uuidYml.getString(uuid);
		}
		return uuid;
	}

	public static void putName(UUID uuid, String name) {
		uuidYml.set(uuid.toString(), name);
	}

}
