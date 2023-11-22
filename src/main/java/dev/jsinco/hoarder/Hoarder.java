package dev.jsinco.hoarder;

import dev.jsinco.hoarder.commands.CommandManager;
import dev.jsinco.hoarder.events.Listeners;
import dev.jsinco.hoarder.manager.FileManager;
import dev.jsinco.hoarder.manager.Settings;
import dev.jsinco.hoarder.storage.DataManager;
import dev.jsinco.hoarder.storage.StorageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hoarder extends JavaPlugin {

    private static Hoarder plugin;
    DataManager dataManager;


    @Override
    public void onEnable() {
        plugin = this;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        FileManager.generateFolder("guis");
        FileManager fileManager = new FileManager("guis/treasure.yml");
        fileManager.generateYamlFile();

        getCommand("hoardertwo").setExecutor(new CommandManager(this));

        getServer().getPluginManager().registerEvents(new Listeners(this), this);


        this.dataManager = Settings.getDataManger();
        dataManager.setEventMaterial(Material.EMERALD_BLOCK);
        System.out.println(dataManager.getEventMaterial());
        dataManager.setEventEndTime(2000);

        //dataManager.addTreasureItem("paper", 30, new ItemStack(Material.PAPER));
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
}
