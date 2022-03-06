package io.github.badnotice.economy.account;

import io.github.badnotice.economy.EconomyPlugin;
import io.github.badnotice.economy.dao.AccountRepository;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AccountRegistry {

    private static final Comparator<Account> ACCOUNT_COMPARATOR = Comparator.comparingDouble(Account::getBalance);

    private final Map<UUID, Account> accountMap;

    public AccountRegistry(EconomyPlugin plugin) {
        this.accountMap = new HashMap<>();

        final AccountRepository accountRepository = plugin.getAccountRepository();
        for (Account account : accountRepository.selectAll()) {
            registerAccount(account);
        }
    }

    public void registerAccount(Account account) {
        this.accountMap.put(account.getUniqueId(), account);
    }

    public Account getAccount(String name) {
        return accountMap
                .values()
                .stream()
                .filter(account -> account.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Account getAccount(Player player) {
        return getAccount(player.getName());
    }

    public void removeAccount(Account account){
        accountMap.remove(account.getUniqueId());
    }

    public List<Account> getTop(int limit){
        return accountMap
                .values()
                .stream()
                .sorted(ACCOUNT_COMPARATOR)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Iterator<Account> getIterator() {
        return accountMap
                .values()
                .iterator();
    }

}
