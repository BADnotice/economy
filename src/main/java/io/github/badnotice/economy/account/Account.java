package io.github.badnotice.economy.account;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class Account {

    public static Account create(String playerName) {
        return create(Bukkit.getPlayer(playerName));
    }

    public static Account create(Player player) {
        return new Account(player.getUniqueId(), player.getName());
    }

    @EqualsAndHashCode.Include
    private final UUID uniqueId;
    private final String name;

    private double balance;
    private boolean dirty;

    public Account(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    /**
     * add an amount of coins to the account.
     *
     * @param amount balance
     */
    public void addBalance(double amount) {
        balance += amount;
        dirty = true;
    }

    /**
     * remove an amount of coins to the account.
     *
     * @param amount balance
     */
    public void removeBalance(double amount) {
        balance -= amount;
        if (balance < 0) {
            balance = 0;
        }

        dirty = true;
    }

    /**
     * set an amount of coins to the account;
     *
     * @param amount balance
     */
    public void setBalance(double amount) {
        balance = amount;
        dirty = true;
    }

}
