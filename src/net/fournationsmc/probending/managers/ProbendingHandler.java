package net.fournationsmc.probending.managers;

import net.fournationsmc.probending.PBMessenger;
import net.fournationsmc.probending.Probending;
import net.fournationsmc.probending.game.Game;
import net.fournationsmc.probending.libraries.database.Callback;
import net.fournationsmc.probending.objects.PBPlayer;
import net.fournationsmc.probending.objects.PBPlayerScoreboard;
import net.fournationsmc.probending.objects.ProbendingField;
import net.fournationsmc.probending.storage.DBProbendingPlayer;
import net.fournationsmc.probending.storage.FFProbendingField;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ProbendingHandler implements Listener {

    protected Map<UUID, PBPlayer> players;

    protected List<ProbendingField> availableFields;
    protected Set<Game> games;

    private FFProbendingField _fieldStorage;
    private DBProbendingPlayer _playerStorage;

    public ProbendingHandler(JavaPlugin plugin) {
        this.players = new HashMap<>();
        this.availableFields = new ArrayList<>();
        this.games = new HashSet<>();
        _fieldStorage = new FFProbendingField(plugin);
        _playerStorage = new DBProbendingPlayer(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadFields();
    }

    public boolean isInGame(Player player) {
        for (Game game : games) {
            if (game.isPlayerInMatch(player)) {
                return true;
            }
        }

        return false;
    }

    public boolean addField(ProbendingField field) {
        if (!availableFields.contains(field)) {
            _fieldStorage.insertField(field);
            availableFields.add(field);
        }
        return true;
    }

    public void removeField(ProbendingField field) {
        if (availableFields.contains(field)) {
            availableFields.remove(field);
            _fieldStorage.deleteField(field);
        }
    }

    public void saveField(ProbendingField field) {
        _fieldStorage.deleteField(field);
        _fieldStorage.insertField(field);
    }

    public ProbendingField getField(Player player, String fieldNumber) {
        for (ProbendingField f : availableFields) {
            if (f.getFieldName().equals(fieldNumber)) {
                return f;
            }
        }
        for (Game game : games) {
            if (game.getField().getFieldName().equals(fieldNumber)) {
                PBMessenger.sendMessage(player, ChatColor.RED + "Field is in use, and cannot be edited!", true);
                return null;
            }
        }
        PBMessenger.sendMessage(player, ChatColor.RED + "Field could not be found!", true);
        return null;
    }

    public Map<ProbendingField, Boolean> getAllFields() {
        Map<ProbendingField, Boolean> fields = new HashMap<>();
        for (ProbendingField f : availableFields) {
            fields.put(f, true);
        }
        for (Game game : games) {
            fields.put(game.getField(), false);
        }
        return fields;
    }

    private void loadFields() {
        availableFields = _fieldStorage.loadFields();
    }

    public void getOfflinePBPlayer(UUID uuid, final Callback<PBPlayer> callback) {
        _playerStorage.loadPBPlayerAsync(uuid, new Callback<PBPlayer>() {
            public void run(PBPlayer player) {
                callback.run(player);
            }
        });
    }

    public void updatePBPlayer(PBPlayer player) {
        _playerStorage.updatePBPlayerAsync(player);
    }

    public void getPlayerInfo(Player player, Player infoPlayer) {
        if (infoPlayer == null) {
            PBMessenger.sendMessage(player, PBMessenger.PBMessage.NOPLAYER);
            return;
        }
        if (players.containsKey(infoPlayer.getUniqueId())) {
            PBPlayerScoreboard pbps = PBPlayerScoreboard.getFromPlayer(player);
            if (pbps == null) {
                pbps = new PBPlayerScoreboard(player);
            }
            pbps.displayInfo(infoPlayer);
        } else {
            PBMessenger.sendMessage(player, PBMessenger.PBMessage.ERROR);
        }
    }

    public PBPlayer getPBPlayer(UUID uuid) {
        return players.containsKey(uuid) ? players.get(uuid) : null;
    }

    @EventHandler
    private void playerLogin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        _playerStorage.loadPBPlayerAsync(player.getUniqueId(), new Callback<PBPlayer>() {
            public void run(PBPlayer pbPlayer) {
                players.put(player.getUniqueId(), pbPlayer);
            }
        });
        Probending.get().getTeamManager().updatePlayerMapsForLogin(player);
        Probending.get().getInviteManager().handleJoin(player);
        new PBPlayerScoreboard(player);
    }

    @EventHandler
    private void playerLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!players.containsKey(player.getUniqueId())) {
            return;
        }
        players.remove(player.getUniqueId());
        Probending.get().getTeamManager().updatePlayerMapsForLogout(player);
        Probending.get().getInviteManager().handleQuit(player);
        PBPlayerScoreboard.removePlayer(player);
    }
}
