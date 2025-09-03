package net.fournationsmc.probending;

import net.fournationsmc.probending.commands.Commands;
import net.fournationsmc.probending.config.ConfigManager;
import com.projectkorra.probending.managers.*;
import net.fournationsmc.probending.managers.*;
import net.fournationsmc.probending.storage.DBProbendingTeam;
import net.fournationsmc.probending.storage.DatabaseHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Probending extends JavaPlugin {

    private static Probending plugin;

    private ProbendingHandler pHandler;
    private PBFieldCreationManager cManager;
    private PBQueueManager qManager;
    private PBTeamManager tManager;
    private InviteManager iManager;

    public static Probending get() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        try {
            new MetricsLite(this);
        } catch (IOException ex) {
            Logger.getLogger(Probending.class.getName()).log(Level.SEVERE, null, ex);
        }
        new ConfigManager();
        new DatabaseHandler(this);
        DBProbendingTeam teamDb = new DBProbendingTeam(this);

        pHandler = new ProbendingHandler(this);
        cManager = new PBFieldCreationManager(pHandler);
        qManager = new PBQueueManager(this, pHandler);
        this.getServer().getPluginManager().registerEvents(cManager, this);
        new Commands(pHandler, cManager, qManager);
        tManager = new PBTeamManager(teamDb);
        iManager = new InviteManager(teamDb);
    }

    public void onDisable() {
        DatabaseHandler.getDatabase().close();
    }

    public ProbendingHandler getProbendingHandler() {
        return pHandler;
    }

    public PBFieldCreationManager getFieldCreationManager() {
        return cManager;
    }

    public PBQueueManager getQueueManager() {
        return qManager;
    }

    public PBTeamManager getTeamManager() {
        return tManager;
    }

    public InviteManager getInviteManager() {
        return iManager;
    }
}
