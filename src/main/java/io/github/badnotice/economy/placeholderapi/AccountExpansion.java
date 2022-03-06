package io.github.badnotice.economy.placeholderapi;

import io.github.badnotice.economy.EconomyPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import io.github.badnotice.economy.account.Account;
import io.github.badnotice.economy.account.AccountRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AccountExpansion extends PlaceholderExpansion {

    private final AccountRegistry accountRegistry;

    public AccountExpansion(EconomyPlugin plugin) {
        accountRegistry = plugin.getAccountRegistry();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "economy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BADnotice";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        final Account account = accountRegistry.getAccount(player);
        if (account == null) {
            return "?";
        }

        if (params.equalsIgnoreCase("balance")){
            return String.valueOf(account.getBalance());
        }

        return super.onPlaceholderRequest(player, params);
    }

}
