package net.fournationsmc.probending.game;

import net.fournationsmc.probending.enums.GamePlayerMode;
import net.fournationsmc.probending.enums.GameType;
import net.fournationsmc.probending.managers.PBQueueManager;
import net.fournationsmc.probending.objects.PBGear;
import net.fournationsmc.probending.objects.PBTeam;
import net.fournationsmc.probending.objects.Pair;
import net.fournationsmc.probending.objects.ProbendingField;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;

public class TeamGame extends Game {

    private PBTeam team1;
    private PBTeam team2;

    public TeamGame(JavaPlugin plugin, PBQueueManager handler, GameType type, GamePlayerMode mode, ProbendingField field, Set<Player> team1Players, Set<Player> team2Players, PBTeam team1, PBTeam team2) {
        super(plugin, handler, type, mode, field, team1Players, team2Players);
        this.team1 = team1;
        this.team2 = team2;
        setupTeamGear();
        startNewRound();
    }

    public PBTeam getTeam1() {
        return team1;
    }

    public PBTeam getTeam2() {
        return team2;
    }

    public void setupTeamGear() {
        gear = new HashMap<>();
        for (Player player : team1Players) {
            PlayerInventory inv = player.getInventory();
            gear.put(player, new Pair<>(inv.getContents(), inv.getArmorContents()));
            PBGear pbGear = new PBGear(team1.getColors()[0], team1.getColors()[1], team1.getColors()[2], team1.getColors()[3]);
            inv.setHelmet(pbGear.Helmet());
            inv.setChestplate(pbGear.Chestplate());
            inv.setLeggings(pbGear.Leggings());
            inv.setBoots(pbGear.Boots());
        }

        for (Player player : team2Players) {
            PlayerInventory inv = player.getInventory();
            gear.put(player, new Pair<>(inv.getContents(), inv.getArmorContents()));
            PBGear pbGear = new PBGear(team2.getColors()[0], team2.getColors()[1], team2.getColors()[2], team2.getColors()[3]);
            inv.setHelmet(pbGear.Helmet());
            inv.setChestplate(pbGear.Chestplate());
            inv.setLeggings(pbGear.Leggings());
            inv.setBoots(pbGear.Boots());
        }
    }
}