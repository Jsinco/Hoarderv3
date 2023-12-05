package dev.jsinco.hoarder;

import dev.jsinco.hoarder.commands.CommandManager;
import dev.jsinco.hoarder.events.Listeners;
import dev.jsinco.hoarder.manager.FileManager;
import dev.jsinco.hoarder.manager.Settings;
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


    @Override
    public void onEnable() {
        plugin = this;
        generateFiles();
        HoarderEvent hoarderEvent = new HoarderEvent(this);
        hoarderEvent.reloadHoarderEvent();

        getCommand("hoarder").setExecutor(new CommandManager(this));
        registerCommandAliases();

        getServer().getPluginManager().registerEvents(new Listeners(this), this);


        this.dataManager = Settings.getDataManger();
    }

    @Override
    public void onDisable() {
        if (Settings.getStorageType() == StorageType.MYSQL || Settings.getStorageType() == StorageType.SQLITE) {
            dataManager.closeConnection();
        }
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
