package dev.jsinco.hoarder;

import dev.jsinco.hoarder.commands.CommandManager;
import dev.jsinco.hoarder.events.Listeners;
import dev.jsinco.hoarder.manager.FileManager;
import dev.jsinco.hoarder.manager.Settings;
import dev.jsinco.hoarder.papi.PAPIManager;
import dev.jsinco.hoarder.storage.DataManager;
import dev.jsinco.hoarder.storage.StorageType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.Level;

public final class Hoarder extends JavaPlugin {

    private static Hoarder plugin;
    private static final String[] fileNames = new String[]{"config.yml", "messages.yml", "info.md", "guis/dynamicitems.yml", "guis/main.yml", "guis/treasure.yml", "guis/stats.yml", "guis/treasure_claim.yml", "guis/example.yml"};
    private DataManager dataManager;
    //private PAPIManager papiManager;

    /*
    TODO: What's left to do 12/6/2023
    - Fix paginated gui index out of bounds
    - Allow pagination arrows to be hidden or force hide them
    - Fix Gradients and namings on everything and double check
    - Add config updater
    - Debug and test everything
    - Add PAPI support
    - Add commands, CHECK added SellCommand(TDO) and EventCommand
    - Discord integration via DiscordSRV or save for v1.1.0
    - Fix duplicate items in treasure
    */

    @Override
    public void onEnable() {
        plugin = this;
        //this.papiManager = new PAPIManager(this);
        this.dataManager = Settings.getDataManger();

        generateFiles();
        //papiManager.register();
        HoarderEvent.INSTANCE.reloadHoarderEvent();

        getCommand("hoarder").setExecutor(new CommandManager(this));
        registerCommandAliases();

        getServer().getPluginManager().registerEvents(new Listeners(this), this);

        // first time setup?
        //if ()

    }

    @Override
    public void onDisable() {
        if (Settings.getStorageType() == StorageType.MYSQL || Settings.getStorageType() == StorageType.SQLITE) {
            dataManager.closeConnection();
        }
        //papiManager.unregister();
    }

    public static Hoarder getInstance() {
        return plugin;
    }

    public static void generateFiles() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        FileManager.generateFolder("guis");
        for (String fileName : fileNames) {
            new FileManager(fileName).generateFile();
        }
    }

    private void registerCommandAliases() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            PluginCommand pluginCommand = getCommand("hoarder");

            for (String alias : Settings.commandAliases()) {
                commandMap.register(alias.toLowerCase(), "hoarder", pluginCommand);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().log(Level.SEVERE, "Failed to register command aliases!", e);
        }
    }
}
