package io.github.badnotice.economy.dao.factory;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import io.github.badnotice.economy.EconomyPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class SQLDatabaseFactory {

    private static final File FILE = new File(EconomyPlugin.getInstance().getDataFolder(), "database");

    public static SQLConnector createConnector(ConfigurationSection section) {
        String databaseType = section.getString("type");

        ConfigurationSection typeSection = section.getConfigurationSection(databaseType);
        switch (databaseType) {
            case "sqlite":
                return buildSqliteDatabaseType(typeSection).connect();
            case "mysql":
                return buildMysqlDatabaseType(typeSection).connect();
            default:
                throw new UnsupportedOperationException("database type unsupported!");
        }
    }

    private static SQLiteDatabaseType buildSqliteDatabaseType(ConfigurationSection typeSection) {
        return SQLiteDatabaseType.builder()
                .file(new File(FILE, typeSection.getString("fileName")))
                .build();
    }

    private static MySQLDatabaseType buildMysqlDatabaseType(ConfigurationSection typeSection) {
        return MySQLDatabaseType.builder()
                .address(typeSection.getString("address"))
                .username(typeSection.getString("username"))
                .password(typeSection.getString("password"))
                .database(typeSection.getString("database"))
                .build();
    }

}
