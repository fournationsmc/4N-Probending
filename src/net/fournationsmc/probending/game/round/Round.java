package net.fournationsmc.probending.game.round;

import net.fournationsmc.probending.game.Game;
import net.fournationsmc.probending.game.scoreboard.PBScoreboard;
import net.fournationsmc.probending.libraries.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class Round {

    private final JavaPlugin plugin;
    private final Game game;

    private Timer timer;

    private Set<Player> team1;
    private Set<Player> team2;

    private Boolean isPaused;
    private Boolean isOnCountdown;
    private Integer countdownDuration;
    private Integer roundDuration;

    private PBScoreboard pbScoreboard;

    public Round(JavaPlugin plugin, Game game, PBScoreboard board) {
        this.plugin = plugin;
        this.game = game;
        this.team1 = game.getTeam1Players();
        this.team2 = game.getTeam2Players();
        this.isPaused = false;
        this.isOnCountdown = true;
        this.countdownDuration = 5;
        this.roundDuration = 180;
        this.pbScoreboard = board;
    }

    public Round setRoundDuration(Integer timeInSecs) {
        this.roundDuration = timeInSecs;
        return this;
    }

    public Round setCountdownDuration(Integer timeInSecs) {
        this.countdownDuration = timeInSecs;
        return this;
    }

    public void start() {
        timer = new Timer(plugin) {

            @Override
            public void secondExecute(int curTime) {
                pbScoreboard.setNewTime(curTime);
                if (curTime <= 3) {
                    sendTitle(ChatColor.RED + "" + curTime);
                }
            }

            @Override
            public void execute() {
                if (isOnCountdown) {
                    //Game has started!
                    isOnCountdown = false;
                    this.setTime(roundDuration);
                    pbScoreboard.setNewTime(roundDuration);
                    sendTitle(ChatColor.GREEN + "FIGHT!");
                } else {
                    stopGame();
                }
            }
        };
        timer.start(countdownDuration, 20l);
    }

    private void sendTitle(String message) {
        Title title = Title.title(Component.empty(), Component.text(message));
        for (Player p : team1) {
            p.showTitle(title);
        }
        for (Player p : team2) {
            p.showTitle(title);
        }
    }

    public void stopGame() {
        game.timerEnded();
        timer.stop();
    }

    public void forceStop() {
        //FORCESTOP SHOULD BE IMPLEMENTED!
        timer.stop();
    }

    public boolean pauseGame() {
        if (isOnCountdown) {
            if (!this.isPaused) {
                timer.setPaused(true);
                isPaused = true;
                return true;
            }
        }
        return false;
    }

    public boolean unPauseGame() {
        if (isPaused) {
            timer.setPaused(false);
            isPaused = false;
            return true;
        }
        return false;
    }

    public boolean canDoSomething() {
        if (isOnCountdown || isPaused) {
            return false;
        }
        return true;
    }
}
