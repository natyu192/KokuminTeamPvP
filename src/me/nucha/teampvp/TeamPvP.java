package me.nucha.teampvp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.nucha.kokumin.coin.Coin;
import me.nucha.teampvp.api.TeamPvPApi;
import me.nucha.teampvp.commands.CommandCancelCycle;
import me.nucha.teampvp.commands.CommandCancelStart;
import me.nucha.teampvp.commands.CommandCycle;
import me.nucha.teampvp.commands.CommandEnd;
import me.nucha.teampvp.commands.CommandGameAdmin;
import me.nucha.teampvp.commands.CommandMaps;
import me.nucha.teampvp.commands.CommandMatch;
import me.nucha.teampvp.commands.CommandSetNext;
import me.nucha.teampvp.commands.CommandSkipThisMap;
import me.nucha.teampvp.commands.CommandStaff;
import me.nucha.teampvp.commands.CommandStart;
import me.nucha.teampvp.commands.CommandStats;
import me.nucha.teampvp.game.GameManager;
import me.nucha.teampvp.game.KitManager;
import me.nucha.teampvp.game.MatchState;
import me.nucha.teampvp.game.NavigatorManager;
import me.nucha.teampvp.game.TeamManager;
import me.nucha.teampvp.game.objective.GameObjectiveManager;
import me.nucha.teampvp.game.stats.StatsManager;
import me.nucha.teampvp.game.tnt.TNTManager;
import me.nucha.teampvp.listeners.BlockProtectionListener;
import me.nucha.teampvp.listeners.ChatListener;
import me.nucha.teampvp.listeners.CoinDropListener;
import me.nucha.teampvp.listeners.GuiTeamSelector;
import me.nucha.teampvp.listeners.KillListener;
import me.nucha.teampvp.listeners.PlayerListener;
import me.nucha.teampvp.listeners.RegionListener;
import me.nucha.teampvp.listeners.TNTListener;
import me.nucha.teampvp.listeners.TutorialListener;
import me.nucha.teampvp.listeners.anni.AnniLocationManager;
import me.nucha.teampvp.listeners.ctw.CTWWoolManager;
import me.nucha.teampvp.listeners.dtm.DTMMonumentManager;
import me.nucha.teampvp.listeners.tdm.TDMScoreManager;
import me.nucha.teampvp.map.MapInfo;
import me.nucha.teampvp.map.MapManager;
import me.nucha.teampvp.map.region.RegionManager;
import me.nucha.teampvp.staff.StaffListener;
import me.nucha.teampvp.staff.StaffManager;
import me.nucha.teampvp.utils.ColorUtils;
import me.nucha.teampvp.utils.ConfigUtil;
import me.nucha.teampvp.utils.ScoreboardUtils;
import me.nucha.teampvp.utils.SoulBound;
import me.nucha.teampvp.utils.SymbolUtils;
import me.nucha.teampvp.utils.UUIDUtils;

public class TeamPvP extends JavaPlugin {

	private static TeamPvP plugin;
	private static ConsoleCommandSender console;
	private static String prefix;
	private List<MapInfo> mapInfos;

	private MapManager mapManager;
	private KitManager KitManager;
	private GameManager gameManager;
	private TeamManager teamManager;
	private RegionManager regionManager;
	private StatsManager statsManager;
	private TNTManager tntManager;
	private DTMMonumentManager dtmMonumentManager;
	private TDMScoreManager tdmScoreManager;
	private GameObjectiveManager gameObjectiveManager;
	private CTWWoolManager ctwWoolManager;
	private AnniLocationManager anniLocationManager;

	public static boolean pl_lunachat;
	public static boolean pl_kokuminserver;

	@Override
	public void onEnable() {
		plugin = this;
		TeamPvPApi.plugin(plugin);
		saveDefaultConfig();
		ConfigUtil.init(plugin);
		saveResource("sampleconfig.yml", false);
		console = getServer().getConsoleSender();
		prefix = "§8[§aTeam PvP§8] §r";
		mapInfos = new ArrayList<MapInfo>();
		MatchState.setState(MatchState.WAITING);

		if (!loadMaps()) {
			return;
		}
		loadManagers();
		loadListeners();
		loadCommands();

		pl_lunachat = Bukkit.getPluginManager().getPlugin("LunaChat") != null;
		pl_kokuminserver = Bukkit.getPluginManager().getPlugin("KokuminServer") != null;
	}

	@Override
	public void onDisable() {
		UUIDUtils.shutdown();
		StaffManager.shutdown();
	}

	private boolean loadMaps() {
		File fileMaps = new File(getDataFolder() + "/maps");
		if (fileMaps.mkdir()) {
			sendConsoleMessage("§amapsファイルを生成しました: " + getDataFolder() + "/maps");
			sendConsoleMessage("§cmapsファイルにマップを入れて、再起動してください");
			getServer().getPluginManager().disablePlugin(this);
			return false;
		} else {
			List<File> mapFiles = Arrays.asList(fileMaps.listFiles());
			List<String> mapRotation = ConfigUtil.map_rotation;
			if (!(mapFiles.size() > 0)) {
				sendConsoleMessage("§cmapsファイルにマップ(world)がひとつもありません\n§cプラグインを無効化します");
				getServer().getPluginManager().disablePlugin(this);
				return false;
			}
			if (mapRotation == null) {
				sendConsoleMessage("§cconfig.ymlにローテーション設定がされていません\n§cプラグインを無効化します");
				List<String> sampleRotation = new ArrayList<>();
				sampleRotation.add("Map 1");
				sampleRotation.add("Map 2");
				getConfig().set("map-rotation", sampleRotation);
				saveConfig();
				getServer().getPluginManager().disablePlugin(this);
				return false;
			}

			for (String mapNameRot : mapRotation) {
				for (File mapFile : mapFiles) {
					if (mapFile.getName().equalsIgnoreCase(mapNameRot)) {
						File configFile = new File(mapFile + "/config.yml");
						if (!configFile.exists()) {
							sendConsoleMessage("§b" + mapFile.getName() + " §8- §6読み込み失敗: \n§cマップフォルダにconfig.ymlという設定ファイルが見つかりません");
							continue;
						}
						FileConfiguration configYml = YamlConfiguration.loadConfiguration(configFile);
						if (!configYml.isSet("name")) {
							sendConsoleMessage("§b" + mapFile.getName() + " §8- §6読み込み失敗: \n§cname: が設定されていません");
							continue;
						}
						String mapName = configYml.getString("name");
						if (!containsFileNamed(mapFile, mapName)) {
							sendConsoleMessage("§b" + mapFile.getName() + " §8- §6読み込み失敗: \n§c" + mapName
									+ "という名前のワールドのファイルが見つかりません");
							continue;
						}
						if (!configYml.isSet("teams")) {
							sendConsoleMessage("§b" + mapFile.getName() + " §8- §6読み込み失敗: \n§cteams: が設定されていません");
							continue;
						}
						sendConsoleMessage("§b" + mapFile.getName() + " §8- §a読み込み成功");
						MapInfo mapInfo = new MapInfo(mapFile, plugin);
						mapInfos.add(mapInfo);
					}
				}
			}
			if (mapInfos.isEmpty()) {
				sendConsoleMessage("§cローテーションに設定されているマップ(world)で読み込めるものがありませんでした\n§cプラグインを無効化します");
				getServer().getPluginManager().disablePlugin(this);
				return false;
			}
			sendConsoleMessage("§aマップを読み込みました");
			return true;
		}
	}

	private void loadManagers() {
		ColorUtils.init(this);
		UUIDUtils.init(this);
		mapManager = new MapManager(this);
		gameManager = new GameManager(this);
		teamManager = new TeamManager(this);
		mapManager.setCurrentMap(mapInfos.get(0).getName());
		regionManager = new RegionManager();
		statsManager = new StatsManager(this);
		tntManager = new TNTManager(this);

		gameObjectiveManager = new GameObjectiveManager();
		tdmScoreManager = new TDMScoreManager(100);
		dtmMonumentManager = new DTMMonumentManager(this);
		ctwWoolManager = new CTWWoolManager(this);
		anniLocationManager = new AnniLocationManager(this);

		ScoreboardUtils.plugin(this);
		StaffManager.init(this);
		NavigatorManager.init(this);
	}

	private void loadCommands() {
		registerCommand("stats", new CommandStats(this), "戦績を表示します");
		registerCommand("maps", new CommandMaps(this), "使用されているマップ一覧を表示します");
		registerCommand("gamestart", new CommandStart(this), "試合を開始します");
		registerCommand("gameend", new CommandEnd(this), "試合を強制終了します");
		registerCommand("cancelstart", new CommandCancelStart(this), "試合開始をキャンセルします");
		registerCommand("cancelcycle", new CommandCancelCycle(this), "次のマップへの移動をキャンセルします");
		registerCommand("cycle", new CommandCycle(this), "次のマップへ移動します");
		registerCommand("skipthismap", new CommandSkipThisMap(this), "試合開始前に現在のマップをやらずに、次のマップへ移動します");
		registerCommand("setnext", new CommandSetNext(this), "次のマップを指定します");
		registerCommand("gameadmin", new CommandGameAdmin(this), "ゲーム管理用のコマンドです");
		registerCommand("match", new CommandMatch(this), "試合の情報を表示します");
		registerCommand("staff", new CommandStaff(this), "管理者モードを切り替えます");
	}

	private void registerCommand(String commandName, CommandExecutor executor, String description) {
		PluginCommand cmd = getCommand(commandName);
		if (cmd != null) {
			cmd.setExecutor(executor);
			cmd.setDescription(description);
		}
	}

	private void loadListeners() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new SoulBound(), this);
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new CoinDropListener(this), this);
		pm.registerEvents(new TutorialListener(this), this);
		pm.registerEvents(new GuiTeamSelector(this), this);
		pm.registerEvents(new KillListener(this), this);
		pm.registerEvents(new ChatListener(this), this);
		pm.registerEvents(new RegionListener(this), this);
		pm.registerEvents(new TNTListener(this), this);
		pm.registerEvents(new BlockProtectionListener(), this);
		pm.registerEvents(new StaffListener(), this);
	}

	public static TeamPvP getInstance() {
		return plugin;
	}

	public static ConsoleCommandSender getConsoleSender() {
		return console;
	}

	public boolean containsFileNamed(File file, String name) {
		for (File f : Arrays.asList(file.listFiles())) {
			if (f.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public File getFileByNameFromFile(File file, String name) {
		for (File f : Arrays.asList(file.listFiles())) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	public List<MapInfo> getMapInfos() {
		return mapInfos;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public KitManager getKitManager() {
		return KitManager;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public TeamManager getTeamManager() {
		return teamManager;
	}

	public TDMScoreManager getTdmScoreManager() {
		return tdmScoreManager;
	}

	public RegionManager getRegionManager() {
		return regionManager;
	}

	public StatsManager getStatsManager() {
		return statsManager;
	}

	public TNTManager getTntManager() {
		return tntManager;
	}

	public DTMMonumentManager getDtmMonumentManager() {
		return dtmMonumentManager;
	}

	public void setDtmMonumentManager(DTMMonumentManager dtmMonumentManager) {
		this.dtmMonumentManager = dtmMonumentManager;
	}

	public void setTdmScoreManager(TDMScoreManager tdmScoreManager) {
		this.tdmScoreManager = tdmScoreManager;
	}

	public void setKitManager(KitManager kitManager) {
		KitManager = kitManager;
	}

	public GameObjectiveManager getGameObjectiveManager() {
		return gameObjectiveManager;
	}

	public void setGameObjectiveManager(GameObjectiveManager gameObjectiveManager) {
		this.gameObjectiveManager = gameObjectiveManager;
	}

	public CTWWoolManager getCtwWoolManager() {
		return ctwWoolManager;
	}

	public void setCtwWoolManager(CTWWoolManager ctwWoolManager) {
		this.ctwWoolManager = ctwWoolManager;
	}

	public AnniLocationManager getAnniLocationManager() {
		return anniLocationManager;
	}

	public void setAnniLocationManager(AnniLocationManager anniLocationManager) {
		this.anniLocationManager = anniLocationManager;
	}

	public static String getPrefix() {
		return prefix;
	}

	public static void sendConsoleMessage(String text) {
		for (String s : text.split("\n"))
			console.sendMessage(prefix + s);
	}

	public static void sendWarnMessage(Player p, String text) {
		p.sendMessage("§e" + SymbolUtils.warn() + " §c" + text);
	}

	public static void addCoin(Player p, int amount, String message) {
		CoinDropListener.drop(p, p.getLocation().add(0, 1.9, 0));
		if (pl_kokuminserver && message != null && !message.isEmpty()) {
			Coin.addCoin(p, amount, message);
		}
	}

	public static void addCoin(Player k, int i) {
		addCoin(k, i, null);
	}

}
