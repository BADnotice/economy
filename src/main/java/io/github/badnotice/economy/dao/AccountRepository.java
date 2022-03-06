package io.github.badnotice.economy.dao;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import io.github.badnotice.economy.dao.adapter.AccountAdapter;
import io.github.badnotice.economy.EconomyPlugin;
import io.github.badnotice.economy.account.Account;
import io.github.badnotice.economy.account.AccountRegistry;

import java.util.Iterator;
import java.util.Set;

public class AccountRepository {

    private static final String TABLE = "account_economy";

    private final EconomyPlugin plugin;

    public AccountRepository(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    public void createTable() {
        this.executor().updateQuery(
                "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                        "unique_id VARCHAR(36) NOT NULL PRIMARY KEY," +
                        "player_name VARCHAR(16) NOT NULL," +
                        "balance DOUBLE NOT NULL" +
                        ");"
        );
    }

    public Account selectOne(String playerName) {
        return this.executor().resultOneQuery(
                "SELECT * FROM " + TABLE + " WHERE player_name = ?",
                statement -> statement.set(1, playerName),
                AccountAdapter.class
        );
    }

    public Set<Account> selectAll() {
        return this.executor().resultManyQuery(
                "SELECT * FROM " + TABLE,
                k -> {

                },
                AccountAdapter.class
        );
    }

    public void insertOne(Account account) {
        this.executor().updateQuery(
                "REPLACE INTO " + TABLE + " VALUES(?, ?, ?)",
                statement -> {
                    statement.set(1, account.getUniqueId().toString());
                    statement.set(2, account.getName());
                    statement.set(3, account.getBalance());
                }
        );
    }

    public void close() {
        final AccountRegistry accountRegistry = plugin.getAccountRegistry();
        final Iterator<Account> iterator = accountRegistry.getIterator();

        while (iterator.hasNext()) {
            final Account next = iterator.next();
            if (!next.isDirty()) {
                continue;
            }

            insertOne(next);
        }

    }

    private SQLExecutor executor() {
        return new SQLExecutor(plugin.getSqlConnector());
    }

}
