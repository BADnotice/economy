package io.github.badnotice.economy.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import io.github.badnotice.economy.Constants;
import io.github.badnotice.economy.EconomyPlugin;
import io.github.badnotice.economy.account.Account;
import io.github.badnotice.economy.account.AccountRegistry;
import io.github.badnotice.economy.util.MoneyFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand  {

    private final AccountRegistry accountRegistry;

    public MoneyCommand(EconomyPlugin plugin) {
        accountRegistry = plugin.getAccountRegistry();
    }

    @Command(
            name = "money"
    )
    public void executeMoney(Context<Player> context, @Optional Player target) {
        Account result = accountRegistry.getAccount(context.getSender());

        if (target != null) {
            result = accountRegistry.getAccount(target);
        }

        context.sendMessage("§aO jogador " + result.getName() + " possui " + MoneyFormatter.apply(result.getBalance()) + " de coins.");
    }

    @Command(
            name = "money.top"
    )
    public void executeMoneyTop(Context<Player> context) {
        context.sendMessage("");
        context.sendMessage(" §aJogadores mais ricos do mundo!");
        context.sendMessage("");

        int counter = 0;
        for (Account account : accountRegistry.getTop(10)) {
            context.sendMessage(" §f" + (counter + 1) + "º " + account.getName() + " §7(" + MoneyFormatter.apply(account.getBalance()) + ")");
            counter++;
        }
        context.sendMessage("");
    }

    @Command(
            name = "money.pay"
    )
    public void executeMoneyPay(Context<Player> context, Player target, double amount) {
        final Account senderAccount = accountRegistry.getAccount(context.getSender());
        if (senderAccount == null) {
            return;
        }

        final Account targetAccount = accountRegistry.getAccount(target);
        if (targetAccount == null) {
            return;
        }

        if (senderAccount.equals(targetAccount)) {
            context.sendMessage("§cVocê não pode enviar coins para si mesmo.");
            return;
        }

        if (amount <= 0){
            context.sendMessage("§cNúmero inserido é inválido.");
            return;
        }

        senderAccount.removeBalance(amount);
        targetAccount.addBalance(amount);

        context.sendMessage("§aVocê enviou " + MoneyFormatter.apply(amount) + " coins para o jogador " + target.getName());
        target.sendMessage("§aVocê recebeu " + MoneyFormatter.apply(amount) + " coins do jogador " + senderAccount.getName());
    }

    @Command(
            name = "money.add",
            permission = Constants.ADMINISTRATOR_PERMISSION,
            usage = "money add <player> <amount>"
    )
    public void executeMoneyAdd(Context<CommandSender> context, Player target, double amount){
        processing(context.getSender(), target, amount, OperationType.ADD);
    }

    @Command(
            name = "money.set",
            permission = Constants.ADMINISTRATOR_PERMISSION,
            usage = "money set <player> <amount>"
    )
    public void executeMoneySet(Context<CommandSender> context, Player target, double amount){
        processing(context.getSender(), target, amount, OperationType.SET);
    }

    @Command(
            name = "money.remove",
            permission = Constants.ADMINISTRATOR_PERMISSION,
            usage = "money remove <player> <amount>"
    )
    public void executeMoneyRemove(Context<CommandSender> context, Player target, double amount){
        processing(context.getSender(), target, amount, OperationType.ADD);
    }

    private boolean processing(CommandSender sender, Player player, double amount, OperationType type) {
        final Account account = accountRegistry.getAccount(player);
        if (account == null) {
            sender.sendMessage("§cJogador não foi encontrado.");
            return false;
        }

        switch (type){
            case ADD:
               account.addBalance(amount);
                break;
            case SET:
                account.setBalance(amount);
                break;
            case REMOVE:
                account.removeBalance(amount);
                break;
        }

        sender.sendMessage("§eVocê alterou os coins do jogador " + account.getName() + " para " + account.getBalance());
        return true;
    }

    enum OperationType {
        ADD,
        SET,
        REMOVE
    }

}
