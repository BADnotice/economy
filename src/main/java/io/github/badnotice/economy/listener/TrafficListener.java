package io.github.badnotice.economy.listener;

import io.github.badnotice.economy.EconomyPlugin;
import io.github.badnotice.economy.account.Account;
import io.github.badnotice.economy.dao.AccountRepository;
import io.github.badnotice.economy.account.AccountRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.Executors;

public class TrafficListener implements Listener {

    private final AccountRepository accountRepository;
    private final AccountRegistry accountRegistry;

    public TrafficListener(EconomyPlugin plugin) {
        this.accountRepository = plugin.getAccountRepository();
        this.accountRegistry = plugin.getAccountRegistry();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.runAsync(() -> {
            Account account = accountRepository.selectOne(player.getName());
            if (account == null) {
                account = Account.create(player);
                accountRepository.insertOne(account);
            }

            accountRegistry.registerAccount(account);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.runAsync(() -> {
            Account account = accountRegistry.getAccount(player);
            if (account != null) {
                accountRegistry.removeAccount(account);
                accountRepository.insertOne(account);
            }
        });
    }

    protected void runAsync(Runnable task) {
        Executors.newSingleThreadExecutor()
                .submit(task);
    }

}
