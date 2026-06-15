package net.fournationsmc.probending.storage;

import net.fournationsmc.probending.config.ConfigManager;
import net.fournationsmc.probending.libraries.database.AbstractDatabase;
import net.fournationsmc.probending.libraries.database.MySQLDatabase;
import net.fournationsmc.probending.libraries.database.SQLiteDatabase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseHandler {

    private static DatabaseHandler INSTANCE;

    private AbstractDatabase _database;
    private boolean _useMySQL = false;

    public DatabaseHandler(JavaPlugin plugin) {
        if (INSTANCE != null) {
            return;
        }

        FileConfiguration config = ConfigManager.defaultConfig.get();
        _useMySQL = config.getBoolean("Database.MySQL.Enabled")
                || "mysql".equalsIgnoreCase(config.getString("General.Storage", "sqlite"));

        if (!_useMySQL) {
            _database = new SQLiteDatabase(plugin.getLogger(), "probending.db", plugin.getDataFolder().getAbsolutePath());
        } else {
            String hostname = config.getString("Database.MySQL.Hostname", config.getString("MySQL.Host", "localhost"));
            String port = String.valueOf(config.getInt("Database.MySQL.Port", config.getInt("MySQL.Port", 3306)));
            String databaseName = config.getString("Database.MySQL.Database", config.getString("MySQL.DB", "Probending"));
            String username = config.getString("Database.MySQL.Username", config.getString("MySQL.User", "ProbendingROOT"));
            String password = config.getString("Database.MySQL.Password", config.getString("MySQL.Pass", "ROOTPassword"));
            _database = new MySQLDatabase(plugin.getLogger(), hostname, port, databaseName, username, password);
        }

        INSTANCE = this;
    }

    public static AbstractDatabase getDatabase() {
        return INSTANCE._database;
    }

    public static Boolean isMySQL() {
        return INSTANCE._useMySQL;
    }
}
