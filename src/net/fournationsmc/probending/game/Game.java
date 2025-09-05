package net.fournationsmc.probending.game;

import net.fournationsmc.probending.enums.GamePlayerMode;
import net.fournationsmc.probending.enums.GameType;
import net.fournationsmc.probending.enums.TeamColor;
import net.fournationsmc.probending.enums.WinningType;
import net.fournationsmc.probending.game.field.FieldManager;
import net.fournationsmc.probending.game.round.Round;
import net.fournationsmc.probending.game.scoreboard.PBScoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.fournationsmc.probending.managers.PBQueueManager;
import net.fournationsmc.probending.objects.PBGear;
import net.fournationsmc.probending.objects.Pair;
import net.fournationsmc.probending.objects.ProbendingField;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Game {

    private final PBScoreboard pbScoreboard;
    protected Set<Player> team1Players;
    protected Set<Player> team2Players;
    protected Map<Player, Pair<ItemStack[], ItemStack[]>> gear;
    private JavaPlugin plugin;
    private PBQueueManager handler;
    private ProbendingField field;
    private FieldManager fieldManager;
    private GameListener listener;
    private Integer waitTime = 5;
    private Integer rounds;
    private Integer curRound;
    private Integer playTime;
    private Boolean suddenDeath;
    private Boolean hasSuddenDeath;
    private Boolean forcedSuddenDeath;
    private Integer team1Score;
    private Integer team2Score;
    private Round round;
    private GameType type;
    private GamePlayerMode mode;

    public Game(JavaPlugin plugin, PBQueueManager handler, GameType type, GamePlayerMode mode,
                ProbendingField field, Set<Player> team1, Set<Player> team2) {
        this.plugin = plugin;
        this.handler = handler;
        this.type = type;
        this.mode = mode;
        this.rounds = type.getRounds();
        this.curRound = 1;
        this.playTime = type.getPlayTime();
        this.hasSuddenDeath = type.hasSuddenDeath();
        this.forcedSuddenDeath = type.isForcedSuddenDeath();
        this.suddenDeath = false;
        this.field = field;
        this.team1Players = team1;
        this.team2Players = team2;
        this.team1Score = 0;
        this.team2Score = 0;
        this.fieldManager = new FieldManager(this, field);
        this.listener = new GameListener(this);
        this.pbScoreboard = new PBScoreboard(plugin);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        if (!(this instanceof TeamGame)) {
            setupPlayerGear();
            setupPlayerTeams();
            startNewRound();
        }
    }

    public GameType getGameType() {
        return type;
    }

    public GamePlayerMode getGamePlayerMode() {
        return mode;
    }

    public ProbendingField getField() {
        return field;
    }

    public Set<Player> getTeam1Players() {
        return team1Players;
    }

    public Set<Player> getTeam2Players() {
        return team2Players;
    }

    /**
     * @return The amount of time in {@link Integer} that the match can run.
     */
    public Integer getPlayTime() {
        return playTime;
    }

    public Boolean isSuddenDeath() {
        if (forcedSuddenDeath) {
            return true;
        }
        return suddenDeath;
    }

    protected void startNewRound() {
        fieldManager.reset();
        if (!isSuddenDeath()) {
            round = new Round(plugin, this, pbScoreboard);
            round.start();
        }
    }

    public void timerEnded() {
        WinningType winningTeam = fieldManager.getWinningTeam();
        manageEnd(winningTeam);
    }

    public void endRound(WinningType winningTeam) {
        manageEnd(winningTeam);
    }

    private void manageEnd(WinningType winningTeam) {
        boolean ended = false;
        round.forceStop();
        suddenDeath = false;
        if (winningTeam.equals(WinningType.TEAM1)) {
            team1Score++;
        } else if (winningTeam.equals(WinningType.TEAM2)) {
            team2Score++;
        } else {
            if (hasSuddenDeath) {
                suddenDeath = true;
                startNewRound();
                pbScoreboard.setTeamScore(winningTeam == WinningType.TEAM1 ? 1 : 2, winningTeam == WinningType.TEAM1 ? team1Score : team2Score);
                return;
            }
        }
        curRound++;
        Title title = Title.title(Component.text(ChatColor.BLUE + "" + team1Score + ChatColor.WHITE + " - " + ChatColor.RED + "" + team2Score), Component.empty());
        for (Player p : team1Players) {
            p.showTitle(title);
        }
        for (Player p : team2Players) {
            p.showTitle(title);
        }
        pbScoreboard.setTeamScore(winningTeam == WinningType.TEAM1 ? 1 : 2, winningTeam == WinningType.TEAM1 ? team1Score : team2Score);
        if (curRound > rounds) {
            ended = true;
        }
        if (team1Score > Math.ceil(rounds / 2) || team2Score > Math.ceil(rounds / 2)) {
            ended = true;
        }
        if (ended) {
            endGame();
        } else {
            startNewRound();
        }
    }

    private void endGame() {
        WinningType winners = WinningType.DRAW;
        if (team1Score > team2Score) {
            winners = WinningType.TEAM1;
        } else if (team2Score > team1Score) {
            winners = WinningType.TEAM2;
        }
        handler.gameEnded(this, winners);
        HandlerList.unregisterAll(listener);
        resetPlayerTeams();
        removeGear();
        return;
    }

    public boolean isPlayerInMatch(Player player) {
        if (team1Players.contains(player) || team2Players.contains(player)) {
            return true;
        }
        return false;
    }

    protected boolean canPlayerMove(Player player) {
        if (team1Players.contains(player) || team2Players.contains(player)) {
            if (round != null) {
                return round.canDoSomething();
            }
        }
        return true;
    }

    protected void playerMove(Player player, Location from, Location to) {
        if (round != null) {
            fieldManager.playerMove(player, from, to);
        }
    }

    public void setupPlayerGear() {
        gear = new HashMap<>();
        for (Player player : team1Players) {
            PlayerInventory inv = player.getInventory();
            gear.put(player, new Pair<>(inv.getContents(), inv.getArmorContents()));
            PBGear pbGear = new PBGear(TeamColor.BLUE);
            inv.clear();
            inv.setHelmet(pbGear.Helmet());
            inv.setChestplate(pbGear.Chestplate());
            inv.setLeggings(pbGear.Leggings());
            inv.setBoots(pbGear.Boots());
        }

        for (Player player : team2Players) {
            PlayerInventory inv = player.getInventory();
            gear.put(player, new Pair<>(inv.getContents(), inv.getArmorContents()));
            PBGear pbGear = new PBGear(TeamColor.RED);
            inv.clear();
            inv.setHelmet(pbGear.Helmet());
            inv.setChestplate(pbGear.Chestplate());
            inv.setLeggings(pbGear.Leggings());
            inv.setBoots(pbGear.Boots());
        }
    }

    public void removeGear() {
        for (Player player : gear.keySet()) {
            if (!player.isOnline()) {
                continue;
            }
            player.getInventory().setContents(gear.get(player).getFirst());
            player.getInventory().setArmorContents(gear.get(player).getSecond());
        }
        gear.clear();
    }

    private void setupPlayerTeams() {
        for (Player p : team1Players) {
            pbScoreboard.addPlayerToScoreboard(p);
            pbScoreboard.addPlayerToTeam1(p);
        }
        for (Player p : team2Players) {
            pbScoreboard.addPlayerToScoreboard(p);
            pbScoreboard.addPlayerToTeam2(p);
        }
    }

    private void resetPlayerTeams() {
        for (Player p : team1Players) {
            pbScoreboard.removePlayerFromScorebard(p);
        }
        for (Player p : team2Players) {
            pbScoreboard.removePlayerFromScorebard(p);
        }
    }
}
