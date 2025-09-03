package net.fournationsmc.probending.events;

import net.fournationsmc.probending.objects.PBPlayer;
import net.fournationsmc.probending.objects.PBTeam;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class TeamJoinQueueEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancel;
    private PBTeam team;
    private List<PBPlayer> players;

    public TeamJoinQueueEvent(PBTeam team, List<PBPlayer> players) {
        this.team = team;
        this.players = players;
    }

    public PBTeam getTeam() {
        return team;
    }

    public List<PBPlayer> getPlayers() {
        return players;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

}
