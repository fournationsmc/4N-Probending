package net.fournationsmc.probending.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigManager {

    public static Config defaultConfig;
    //public static Config messageConfig;

    public ConfigManager() {
        defaultConfig = new Config(new File("config.yml"));
        //messageConfig = new Config(new File("language.yml"));
        configCheck(ConfigType.DEFAULT);
        //configCheck(ConfigType.MESSAGES);
    }

    public static void configCheck(ConfigType type) {
        FileConfiguration config;
        switch (type) {
            case DEFAULT:
                config = defaultConfig.get();

                // Preserve compatibility with the legacy checked-in config structure
                // while also writing the newer Database.MySQL.* keys expected by code.
                String legacyStorage = config.getString("General.Storage", "sqlite");
                boolean useMySql = "mysql".equalsIgnoreCase(legacyStorage);
                config.addDefault("Database.MySQL.Enabled", useMySql);
                config.addDefault("Database.MySQL.Hostname", config.getString("MySQL.Host", "localhost"));
                config.addDefault("Database.MySQL.Port", config.getInt("MySQL.Port", 3306));
                config.addDefault("Database.MySQL.Database", config.getString("MySQL.DB", "Probending"));
                config.addDefault("Database.MySQL.Username", config.getString("MySQL.User", "ProbendingROOT"));
                config.addDefault("Database.MySQL.Password", config.getString("MySQL.Pass", "ROOTPassword"));

                defaultConfig.save();
                break;
            //case MESSAGES:
            //config = messageConfig.get();

            //I'll do this later...
                /*
                config.addDefault("Messages.General.Prefix", "&7[&6Probending&7] ");
                config.addDefault("Messages.General.NoPermission", "&cYou dont have permission to do that.");
                config.addDefault("Messages.General.ConfigReloaded", "&aConfiguration reloaded.");
                config.addDefault("Messages.General.NoTeamPermissions", "&cYou dont have permission for any team commands.");
                
                config.addDefault("Messages.Player.NoBendingType", "");
                config.addDefault("Messages.Player.MultipleBendingTypes", "");
                config.addDefault("Messages.Player.PlayerNotElement", "");
                config.addDefault("Messages.Player.PlayerAlreadyInTeam", "");
                config.addDefault("Messages.Player.ElementNotAllowed", "");
                config.addDefault("Messages.Player.PlayerNotInTeam", "");
                config.addDefault("Messages.Player.PlayerNotOnline", "");
                config.addDefault("Messages.Player.PlayerInviteSent", "");
                config.addDefault("Messages.Player.PlayerInviteReceived", "");
                config.addDefault("Messages.Player.InviteInstructions", "");
                config.addDefault("Messages.Player.NoInviteFromTeam", "");
                config.addDefault("Messages.Player.YouHaveBeenBooted", "");
                config.addDefault("Messages.Player.YouHaveQuit", "");
                config.addDefault("Messages.Player.RemovedFromTeamBecauseDifferentElement", "");
                config.addDefault("Messages.Player.RemovedFromTeamBecauseNoElement", "");
                config.addDefault("Messages.Player.ElementChanged", "");
                config.addDefault("Messages.Player.PlayerAddedElement", "");
                */

            //messageConfig.save();
            //break;
        }
    }

    public static FileConfiguration getConfig() {
        return ConfigManager.defaultConfig.get();
    }
}
