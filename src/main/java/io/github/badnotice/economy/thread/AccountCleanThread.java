package io.github.badnotice.economy.thread;

import io.github.badnotice.economy.EconomyPlugin;
import io.github.badnotice.economy.account.Account;
import io.github.badnotice.economy.account.AccountRegistry;
import io.github.badnotice.economy.dao.AccountRepository;

import java.util.Iterator;

public class AccountCleanThread implements Runnable {

    private final AccountRegistry accountRegistry;
    private final AccountRepository accountRepository;

    public AccountCleanThread(EconomyPlugin plugin) {
        this.accountRegistry = plugin.getAccountRegistry();
        this.accountRepository = plugin.getAccountRepository();
    }

    @Override
    public void run() {
        final Iterator<Account> iterator = accountRegistry.getIterator();
        while (iterator.hasNext()) {
            final Account next = iterator.next();
            if (!next.isDirty()) {
                continue;
            }

            accountRepository.insertOne(next);
            next.setDirty(false);
        }
    }

}
