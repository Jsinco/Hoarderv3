package dev.jsinco.hoarder;

import dev.jsinco.hoarder.commands.CommandManager;
import dev.jsinco.hoarder.events.Listeners;
import dev.jsinco.hoarder.manager.FileManager;
import dev.jsinco.hoarder.manager.Settings;
import dev.jsinco.hoarder.papi.PAPIManager;
import dev.jsinco.hoarder.storage.StorageType;
import dev.jsinco.hoarder.utilities.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.logging.Level;

public final class Hoarder extends JavaPlugin {

    private static Hoarder plugin;
    private PAPIManager papiManager; private boolean usePapi = false;

    /*
    TODO: What's left to do 12/6/2023
    - Debug and test everything
    - Add PAPI support
    - Discord integration via DiscordSRV or save for v1.1.0
    - SMALL: add replaceTopPlaceholders for LangMsgs
    - make all database calls async
    */

    @Override
    public void onEnable() {
        plugin = this;
        usePapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        // Register commands and events
        getCommand("hoarder").setExecutor(new CommandManager(this));
        getServer().getPluginManager().registerEvents(new Listeners(this), this);


        // Generate default files, reload Hoarder event/Start one if not running, register command aliases, register PlaceholderAPI
        FileManager.generateDefaultFiles();
        HoarderEvent.INSTANCE.reloadHoarderEvent();
        registerCommandAliases();

        if (usePapi) {
            papiManager = new PAPIManager(this);
            papiManager.register();
        }

        // Update config if needed
        FileManager configFile = new FileManager("config.yml");
        YamlConfiguration config = configFile.getFileYaml();
        if (config.get("config-version") == null || !config.getString("config-version").equals(getDescription().getVersion())) {
            try {
                config.set("config-version", getDescription().getVersion());
                configFile.saveFileYaml();

                ConfigUpdater.update(this, "config.yml", configFile.getFile(), Collections.emptyList());
                getLogger().info("Successfully updated config.yml to v" + getDescription().getVersion() + "!");
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Failed to update config.yml!", e);
            }
        }

        // TODO: first time setup stuff?
    }

    @Override
    public void onDisable() {
        // Close connection to database if using MySQL or SQLite
        if (Settings.getStorageType() == StorageType.MYSQL || Settings.getStorageType() == StorageType.SQLITE) {
            Settings.getDataManger().closeConnection();
        }
        // Unregister PlaceholderAPI
        if (usePapi) papiManager.unregister();
    }

    public static Hoarder getInstance() {
        return plugin;
    }


    private void registerCommandAliases() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            PluginCommand pluginCommand = getCommand("hoarder");

            short total = 0;
            for (String alias : Settings.commandAliases()) {
                commandMap.register(alias.toLowerCase(), "hoarder", pluginCommand);
                total++;
            }
            if (total > 0) getLogger().info("Successfully registered " + total + " command aliases");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().log(Level.SEVERE, "Failed to register command aliases!", e);
        }
    }
}
