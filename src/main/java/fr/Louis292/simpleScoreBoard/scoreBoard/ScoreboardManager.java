package fr.Louis292.simpleScoreBoard.scoreBoard;

import fr.Louis292.simpleScoreBoard.scoreBoard.listeners.ScoreboardListeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private JavaPlugin plugin;

    public ScoreboardManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new ScoreboardListeners(), plugin);
    }

    private static final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();

    public static void updateScoreboard(Player player, String title, List<String> lines) {
        Scoreboard scoreboard = playerScoreboards.get(player.getUniqueId());

        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            playerScoreboards.put(player.getUniqueId(), scoreboard);
        }

        Objective objective = scoreboard.getObjective("sidebar");
        if (objective != null) {
            objective.unregister();
        }

        objective = scoreboard.registerNewObjective("sidebar", "dummy", translateColorCodes(title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = lines.size();
        for (String line : lines) {
            String translatedLine = translateColorCodes(line);

            if (translatedLine.length() > 40) {
                translatedLine = translatedLine.substring(0, 40);
            }

            Team team = scoreboard.getTeam("line_" + score);
            if (team == null) {
                team = scoreboard.registerNewTeam("line_" + score);
            }

            String entry = ChatColor.values()[score % ChatColor.values().length].toString();

            if (!team.hasEntry(entry)) {
                team.addEntry(entry);
            }

            if (translatedLine.length() <= 16) {
                team.setPrefix(translatedLine);
                team.setSuffix("");
            } else {
                team.setPrefix(translatedLine.substring(0, 16));
                team.setSuffix(translatedLine.substring(16));
            }

            objective.getScore(entry).setScore(score);
            score--;
        }

        player.setScoreboard(scoreboard);
    }

    public static void removeScoreboard(Player player) {
        Scoreboard scoreboard = playerScoreboards.remove(player.getUniqueId());

        if (scoreboard != null) {
            Objective objective = scoreboard.getObjective("sidebar");
            if (objective != null) {
                objective.unregister();
            }

            scoreboard.getTeams().forEach(Team::unregister);
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private static String translateColorCodes(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}