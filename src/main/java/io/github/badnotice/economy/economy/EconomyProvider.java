package io.github.badnotice.economy.economy;

import io.github.badnotice.economy.EconomyPlugin;
import io.github.badnotice.economy.account.Account;
import io.github.badnotice.economy.account.AccountRegistry;
import io.github.badnotice.economy.util.MoneyFormatter;
import net.milkbowl.vault.economy.EconomyResponse;

import static net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE;
import static net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS;

public class EconomyProvider extends EconomyWrapper {

    private final EconomyPlugin plugin;

    private final AccountRegistry accountRegistry;

    public EconomyProvider(EconomyPlugin plugin) {
        this.plugin = plugin;
        accountRegistry = plugin.getAccountRegistry();
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public String format(double amount) {
        return MoneyFormatter.apply(amount);
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return accountRegistry.getAccount(playerName) != null;
    }

    @Override
    public double getBalance(String playerName) {
        final Account account = accountRegistry.getAccount(playerName);
        if (account == null) {
            return 0;
        }

        return account.getBalance();
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        final Account account = accountRegistry.getAccount(playerName);
        if (account == null) {
            return new EconomyResponse(0, 0, FAILURE, "Jogador não existente!");
        }

        double currentBalance = account.getBalance();
        double newBalance = currentBalance - amount;
        if (newBalance <= 0) {
            newBalance = 0;
        }

        account.removeBalance(newBalance);
        return new EconomyResponse(currentBalance, newBalance, SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        final Account account = accountRegistry.getAccount(playerName);
        if (account == null) {
            return new EconomyResponse(0, 0, FAILURE, "Jogador não existente!");
        }

        double currentBalance = account.getBalance();
        double newBalance = account.getBalance() + amount;

        account.addBalance(newBalance);
        return new EconomyResponse(currentBalance, newBalance, SUCCESS, null);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (hasAccount(playerName)) {
            return true;
        }

        final Account account = Account.create(playerName);
        accountRegistry.registerAccount(account);
        return false;
    }

}
