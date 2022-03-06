package io.github.badnotice.economy.dao.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import io.github.badnotice.economy.account.Account;

import java.util.UUID;

public class AccountAdapter implements SQLResultAdapter<Account> {

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {
        final UUID uniqueId = UUID.fromString(resultSet.get("unique_id"));
        final String name = resultSet.get("player_name");
        final double balance = resultSet.get("balance");

        final Account account = new Account(uniqueId, name);
        account.setBalance(balance);

        return account;
    }

}
