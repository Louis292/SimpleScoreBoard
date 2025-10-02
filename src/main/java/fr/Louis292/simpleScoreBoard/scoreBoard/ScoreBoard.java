package fr.Louis292.simpleScoreBoard.scoreBoard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScoreBoard {

    private static ScoreBoard instance;

    private final Map<Player, Scoreboard> playerScoreboards = new HashMap<>();
    private final Map<Player, List<String>> playerLines = new HashMap<>();
    private final Map<Player, Objective> playerObjectives = new HashMap<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private ScoreBoard() {}

    public static ScoreBoard getInstance() {
        if (instance == null) {
            instance = new ScoreBoard();
        }
        return instance;
    }

    public void createScoreboard(Player player, String title) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("main", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        playerScoreboards.put(player, scoreboard);
        playerObjectives.put(player, objective);
        playerLines.put(player, new ArrayList<>());

        player.setScoreboard(scoreboard);
    }

    public void addLine(Player player, String line) {
        if (!hasScoreboard(player)) return;

        if (line.length() > 40) {
            line = line.substring(0, 40);
        }

        List<String> lines = playerLines.get(player);
        lines.add(ChatColor.translateAlternateColorCodes('&', line));
        updateScoreboard(player);
    }

    public void refreshScoreboard(Player player) {
        if (!hasScoreboard(player)) return;

        Objective objective = playerObjectives.get(player);
        List<String> lines = playerLines.get(player);

        Set<String> entries = new HashSet<>(objective.getScoreboard().getEntries());
        for (String entry : entries) {
            if (objective.getScore(entry).isScoreSet()) {
                objective.getScoreboard().resetScores(entry);
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int score = lines.size() - i;

            String uniqueLine = line;
            while (objective.getScore(uniqueLine).isScoreSet()) {
                uniqueLine += ChatColor.RESET;
            }

            objective.getScore(uniqueLine).setScore(score);
        }
    }

    public static void autoRefreshPlayerScoreboard(Player player) {
        ScoreBoard manager = getInstance();
        if (!manager.hasScoreboard(player)) return;

        updateExistingDateTimeLine(player);

        modifyLineByPrefix(player, "En ligne:", "&7En ligne: &a" + Bukkit.getOnlinePlayers().size());

        manager.refreshScoreboard(player);
    }

    public static void refreshAllPlayerScoreboards() {
        ScoreBoard manager = getInstance();
        Set<Player> playersWithScoreboards = new HashSet<>(manager.playerScoreboards.keySet());

        for (Player player : playersWithScoreboards) {
            if (player.isOnline()) {
                autoRefreshPlayerScoreboard(player);
            } else {
                manager.removeScoreboard(player);
            }
        }
    }

    public void modifyLine(Player player, int lineIndex, String newLine) {
        if (!hasScoreboard(player)) return;

        List<String> lines = playerLines.get(player);
        if (lineIndex >= 0 && lineIndex < lines.size()) {
            lines.set(lineIndex, ChatColor.translateAlternateColorCodes('&', newLine));
            updateScoreboard(player);
        }
    }

    public void setDateTimeLine(Player player) {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeString = now.format(dateTimeFormatter);
        addLine(player, "&8" + dateTimeString);
    }

    public void updateDateTimeLine(Player player, int lineIndex) {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeString = now.format(dateTimeFormatter);
        modifyLine(player, lineIndex, "&7" + dateTimeString);
    }

    public void clearLines(Player player) {
        if (!hasScoreboard(player)) return;

        playerLines.get(player).clear();
        updateScoreboard(player);
    }

    public void setTitle(Player player, String newTitle) {
        if (!hasScoreboard(player)) return;

        Objective objective = playerObjectives.get(player);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', newTitle));
    }

    public void removeScoreboard(Player player) {
        if (!hasScoreboard(player)) return;

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerScoreboards.remove(player);
        playerObjectives.remove(player);
        playerLines.remove(player);
    }

    public boolean hasScoreboard(Player player) {
        return playerScoreboards.containsKey(player);
    }

    public List<String> getLines(Player player) {
        return hasScoreboard(player) ? new ArrayList<>(playerLines.get(player)) : new ArrayList<>();
    }

    public int getLineCount(Player player) {
        return hasScoreboard(player) ? playerLines.get(player).size() : 0;
    }

    private void updateScoreboard(Player player) {
        if (!hasScoreboard(player)) return;

        Objective objective = playerObjectives.get(player);
        List<String> lines = playerLines.get(player);

        objective.getScoreboard().getEntries().forEach(entry -> {
            if (objective.getScore(entry).isScoreSet()) {
                objective.getScoreboard().resetScores(entry);
            }
        });

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int score = lines.size() - i;

            String uniqueLine = line;
            while (objective.getScore(uniqueLine).isScoreSet()) {
                uniqueLine += ChatColor.RESET;
            }

            objective.getScore(uniqueLine).setScore(score);
        }
    }

    public void createScoreboardWithLines(Player player, String title, String... lines) {
        createScoreboard(player, title);
        for (String line : lines) {
            addLine(player, line);
        }
    }

    public void addEmptyLine(Player player) {
        addLine(player, " ");
    }

    public static void createPlayerScoreboard(Player player, String title) {
        getInstance().createScoreboard(player, title);
    }

    public static void setPlayerTitle(Player player, String newTitle) {
        getInstance().setTitle(player, newTitle);
    }

    public static void removePlayerScoreboard(Player player) {
        getInstance().removeScoreboard(player);
    }

    public static int findLineIndex(Player player, String searchText) {
        List<String> lines = getInstance().getLines(player);
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(searchText)) {
                return i;
            }
        }
        return -1;
    }

    public static int findLineIndexByPrefix(Player player, String prefix) {
        List<String> lines = getInstance().getLines(player);
        String cleanPrefix = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix));

        for (int i = 0; i < lines.size(); i++) {
            String cleanLine = ChatColor.stripColor(lines.get(i));
            if (cleanLine.startsWith(cleanPrefix)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean modifyLineByContent(Player player, String searchText, String newLine) {
        int index = findLineIndex(player, searchText);
        if (index != -1) {
            getInstance().modifyLine(player, index, newLine);
            return true;
        }
        return false;
    }

    public static boolean modifyLineByPrefix(Player player, String prefix, String newLine) {
        int index = findLineIndexByPrefix(player, prefix);
        if (index != -1) {
            getInstance().modifyLine(player, index, newLine);
            return true;
        }
        return false;
    }

    public static void updateOrAddLineByPrefix(Player player, String prefix, String newLine) {
        if (!modifyLineByPrefix(player, prefix, newLine)) {
            getInstance().addLine(player, newLine);
        }
    }

    public static boolean updateExistingDateTimeLine(Player player) {
        List<String> lines = getInstance().getLines(player);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (int i = 0; i < lines.size(); i++) {
            String cleanLine = ChatColor.stripColor(lines.get(i));
            if (cleanLine.matches(".*\\d{2}/\\d{2}/\\d{4}.*")) {
                LocalDateTime now = LocalDateTime.now();
                String dateTimeString = now.format(formatter);
                getInstance().modifyLine(player, i, "&7" + dateTimeString);
                return true;
            }
        }
        return false;
    }

    public void setLigne(Player player, int score, String line) {
        if (!hasScoreboard(player)) return;

        if (line.length() > 40) {
            line = line.substring(0, 40);
        }

        Objective objective = playerObjectives.get(player);
        List<String> lines = playerLines.get(player);

        String existingLine = findLineByScore(player, score);
        if (existingLine != null) {
            objective.getScoreboard().resetScores(existingLine);
            lines.remove(existingLine);
        }

        String formattedLine = ChatColor.translateAlternateColorCodes('&', line);

        String uniqueLine = formattedLine;
        while (objective.getScore(uniqueLine).isScoreSet()) {
            uniqueLine += ChatColor.RESET;
        }

        lines.add(uniqueLine);
        objective.getScore(uniqueLine).setScore(score);
    }

    public static void setPlayerLigne(Player player, int score, String line) {
        getInstance().setLigne(player, score, line);
    }

    private String findLineByScore(Player player, int score) {
        if (!hasScoreboard(player)) return null;

        Objective objective = playerObjectives.get(player);
        for (String entry : objective.getScoreboard().getEntries()) {
            if (objective.getScore(entry).getScore() == score) {
                return entry;
            }
        }
        return null;
    }

    public void removeLigneByScore(Player player, int score) {
        if (!hasScoreboard(player)) return;

        String line = findLineByScore(player, score);
        if (line != null) {
            Objective objective = playerObjectives.get(player);
            List<String> lines = playerLines.get(player);

            objective.getScoreboard().resetScores(line);
            lines.remove(line);
        }
    }

    public static void setPlayerLignes(Player player, Map<Integer, String> lignesWithScores) {
        ScoreBoard manager = getInstance();
        for (Map.Entry<Integer, String> entry : lignesWithScores.entrySet()) {
            manager.setLigne(player, entry.getKey(), entry.getValue());
        }
    }
}