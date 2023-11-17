package dev.jsinco.hoarder;

import dev.jsinco.hoarder.commands.CommandManager;
import dev.jsinco.hoarder.events.Listeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hoarder extends JavaPlugin {

    private static Hoarder plugin;

    @Override
    public void onEnable() {
        plugin = this;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        FileManager.generateFolder("guis");
        FileManager fileManager = new FileManager("guis/main.yml");
        fileManager.generateFile();

        getCommand("hoardertwo").setExecutor(new CommandManager(this));

        getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Hoarder getInstance() {
        return plugin;
    }
}
