package fr.Louis292.simpleScoreBoard.scoreBoard.listeners;

import fr.Louis292.simpleScoreBoard.SimpleScoreBoard;
import fr.Louis292.simpleScoreBoard.scoreBoard.ScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardListeners implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ScoreboardManager.removeScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        List<String> scoreBoard = new ArrayList<>();
        for (String s : SimpleScoreBoard.SCORE_BOARD_LIST) {
            scoreBoard.add(ChatColor.translateAlternateColorCodes('&', s.replace("{PLAYER_NAME}", player.getName())));
        }

        ScoreboardManager.updateScoreboard(player, SimpleScoreBoard.SCORE_BOARD_TITLE, scoreBoard);
    }
}
