package dev.jsinco.hoarder;

import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hoarder extends JavaPlugin {

    private static Hoarder plugin;

    @Override
    public void onEnable() {
        plugin = this;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Hoarder getInstance() {
        return plugin;
    }
}
