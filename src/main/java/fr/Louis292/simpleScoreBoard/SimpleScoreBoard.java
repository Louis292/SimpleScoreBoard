package fr.Louis292.simpleScoreBoard;

import fr.Louis292.simpleScoreBoard.scoreBoard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class SimpleScoreBoard extends JavaPlugin {
    public static FileConfiguration config;

    public static List<String> SCORE_BOARD_LIST = new ArrayList<>();
    public static String SCORE_BOARD_TITLE;

    public static ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        getLogger().info("Starting...");

        this.saveDefaultConfig();
        config = getConfig();

        SCORE_BOARD_TITLE = config.getString("score_board_title");

        SCORE_BOARD_LIST = config.getStringList("score_board");

        scoreboardManager = new ScoreboardManager(this);

        getLogger().info("The plugin was been start !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");

        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreboardManager.removeScoreboard(p);
        }

        getLogger().info("The plugin was been disable");
    }
}
