package fr.Louis292.simpleScoreBoard.scoreBoard.listeners;

import fr.Louis292.simpleScoreBoard.SimpleScoreBoard;
import fr.Louis292.simpleScoreBoard.scoreBoard.ScoreBoard;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class ScoreboardListeners implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ScoreBoard.removePlayerScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        setScoreBoard(player);

        if (SimpleScoreBoard.REFRESH_FOR_ALL_ON_PLAYER_JOIN) {
            ScoreBoard.refreshAllPlayerScoreboards();
        }

        if (SimpleScoreBoard.AUTO_REFRESH_ACTIVE) {
            Bukkit.getScheduler().runTaskTimer(SimpleScoreBoard.plugin, () -> {
                if (player.isOnline()) {
                    updateScoreBoard(player);
                }
            }, 20L * SimpleScoreBoard.AUTO_REFRESH, 20L * SimpleScoreBoard.AUTO_REFRESH);
        }
    }

    private void setScoreBoard(Player player) {
        String title = SimpleScoreBoard.SCORE_BOARD_TITLE;
        ScoreBoard.createPlayerScoreboard(player, title);
        ScoreBoard.setPlayerTitle(player, title);

        updateScoreBoard(player);

        SimpleScoreBoard.log("ScoreBoard created for " + player.getName() + " with " + title + " title");
    }

    private void updateScoreBoard(Player player) {
        HashMap<Integer, String> SCORE_BOARD_MAP = new HashMap<>();
        for (int i = 0; i < SimpleScoreBoard.SCORE_BOARD_LIST.size(); i++) {
            SCORE_BOARD_MAP.put(i, parsePlaceholders(player, SimpleScoreBoard.SCORE_BOARD_LIST.get(i)));
        }
        ScoreBoard.setPlayerLignes(player, SCORE_BOARD_MAP);
        SimpleScoreBoard.log("ScoreBoard updated for " + player.getName());
    }

    private String parsePlaceholders(Player player, String text) {
        text = ChatColor.translateAlternateColorCodes('&', text)
                .replace("{PLAYER_NAME}", player.getName())
                .replace("{PLAYERS}", Bukkit.getOnlinePlayers().size() + "")
        ;

        if (SimpleScoreBoard.USE_PLACEHOLDERS) {
            return PlaceholderAPI.setPlaceholders(player, text);
        } else {
            return text;
        }
    }
}
