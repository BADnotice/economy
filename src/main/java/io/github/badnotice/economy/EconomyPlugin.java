package io.github.badnotice.economy;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import io.github.badnotice.economy.commands.MoneyCommand;
import io.github.badnotice.economy.dao.AccountRepository;
import io.github.badnotice.economy.thread.AccountCleanThread;
import lombok.Getter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;
import io.github.badnotice.economy.account.AccountRegistry;
import io.github.badnotice.economy.dao.factory.SQLDatabaseFactory;
import io.github.badnotice.economy.economy.EconomyProvider;
import io.github.badnotice.economy.listener.TrafficListener;
import io.github.badnotice.economy.placeholderapi.AccountExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

@Getter
public final class EconomyPlugin extends JavaPlugin {

    private Economy economy;
    private SQLConnector sqlConnector;

    private AccountRepository accountRepository;
    private AccountRegistry accountRegistry;

    @Override
    public void onEnable() {
        if (!this.setupEconomy()) {
            onDisable();
            return;
        }

        saveDefaultConfig();

        setupDatabase();
        setupGlobalRegistry();
        setupRunnable();

        registerListeners();
        registerCommands();

        new AccountExpansion(this).register();
    }

    @Override
    public void onDisable() {
        accountRepository.close();
    }

    public static EconomyPlugin getInstance() {
        return getPlugin(EconomyPlugin.class);
    }

    private boolean setupEconomy() {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        RegisteredServiceProvider<Economy> registration = servicesManager.getRegistration(Economy.class);
        if (registration != null) {
            getLogger().info("Economia não foi inicializada pois já existe outro plugin de economia no servidor.");
            return false;
        } else {
            economy = new EconomyProvider(this);
            servicesManager.register(Economy.class, economy, this, ServicePriority.Highest);
            return true;
        }
    }

    private void setupDatabase() {
        sqlConnector = SQLDatabaseFactory.createConnector(getConfig().getConfigurationSection("database"));

        accountRepository = new AccountRepository(this);
        accountRepository.createTable();
    }

    private void setupGlobalRegistry() {
        accountRegistry = new AccountRegistry(this);
    }

    private void setupRunnable() {
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(this, new AccountCleanThread(this), 0L, 300L);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new TrafficListener(this), this);
    }

    private void registerCommands() {
        BukkitFrame bukkitFrame = new BukkitFrame(this);
        bukkitFrame.registerCommands(
              new MoneyCommand(this)
        );
// TUS TUS TUS TUS
        MessageHolder messageHolder = bukkitFrame.getMessageHolder();
        messageHolder.setMessage(MessageType.NO_PERMISSION, "§cVocê não possui permissão para executar este comando.");
        messageHolder.setMessage(MessageType.ERROR, "§cUm erro ocorreu! {error}");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "§cUtilize /{usage}");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "§cVocê não pode utilizar este comando pois ele é direcioado apenas para {target}.");
    }

}
