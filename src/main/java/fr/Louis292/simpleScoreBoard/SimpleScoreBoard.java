package fr.Louis292.simpleScoreBoard;

import fr.Louis292.simpleScoreBoard.scoreBoard.ScoreBoard;
import fr.Louis292.simpleScoreBoard.scoreBoard.listeners.ScoreboardListeners;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class SimpleScoreBoard extends JavaPlugin {
    public static FileConfiguration config;

    public static JavaPlugin plugin;

    public static List<String> SCORE_BOARD_LIST = new ArrayList<>();
    public static String SCORE_BOARD_TITLE;
    public static HashMap<Integer, String> SCORE_BOARD_MAP = new HashMap<>();

    public static boolean debug;

    private static Logger logger;

    public static boolean AUTO_REFRESH_ACTIVE;
    public static int AUTO_REFRESH;

    public static boolean REFRESH_FOR_ALL_ON_PLAYER_JOIN;

    public static boolean USE_PLACEHOLDERS;

    @Override
    public void onEnable() {
        getLogger().info("Starting...");

        plugin = this;

        SimpleScoreBoard.logger = getLogger();

        this.saveDefaultConfig();
        config = getConfig();

        debug = config.getBoolean("debug", false);

        log("Debug: " + debug);

        SCORE_BOARD_TITLE = config.getString("score_board_title", "&eCustom Minecraft Server");

        log(SCORE_BOARD_TITLE);

        SCORE_BOARD_LIST = config.getStringList("score_board");
//        Collections.reverse(SCORE_BOARD_LIST);

        if (debug) {
            log("ScoreBoard content: ");

            for (String s: SCORE_BOARD_LIST) {
                log(s);
            }
        }

        for (int i = 0; i < SCORE_BOARD_LIST.size(); i++) {
            SCORE_BOARD_MAP.put(i, SCORE_BOARD_LIST.get(i));
        }

        AUTO_REFRESH_ACTIVE = config.getBoolean("auto_refresh.active");
        AUTO_REFRESH = config.getInt("auto_refresh.timer");

        log("Auto refresh: " + AUTO_REFRESH_ACTIVE);
        log("Auto refresh timer: " + AUTO_REFRESH);

        REFRESH_FOR_ALL_ON_PLAYER_JOIN = config.getBoolean("refresh_on_player_join");
        log("Refresh for all on player join: " + REFRESH_FOR_ALL_ON_PLAYER_JOIN);

        USE_PLACEHOLDERS = config.getBoolean("use_placeholders", true);
        log("Use place holder: " + USE_PLACEHOLDERS);

        getServer().getPluginManager().registerEvents(new ScoreboardListeners(), this);

        getLogger().info("The plugin was been start !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");

        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreBoard.removePlayerScoreboard(p);
        }

        getLogger().info("The plugin was been disable");
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void log(String content) {
        if (debug) {
            logger.info(content);
        }
    }
}
