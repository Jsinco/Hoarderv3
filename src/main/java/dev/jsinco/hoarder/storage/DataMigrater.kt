package dev.jsinco.hoarder.storage

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.objects.HoarderPlayer
import dev.jsinco.hoarder.objects.TreasureItem
import org.bukkit.Material

class DataMigrater (
    private val newStorageType: StorageType,
){
    companion object {
        val plugin: Hoarder = Hoarder.getInstance()
    }
    // TODO: hoarder cache
    private var dataManager: DataManager = Settings.getDataManger()

    private val hoarderPlayers: List<HoarderPlayer> = dataManager.getAllHoarderPlayers()
    private val treasureItems: List<TreasureItem>? = dataManager.getAllTreasureItems()
    private val eventEndTime: Long = dataManager.getEventEndTime()
    private val activeMaterial: Material = dataManager.getEventMaterial()
    private val activeSellPrice: Double = dataManager.getEventSellPrice()

    init {
        if (newStorageType == StorageType.MYSQL) {
            if (plugin.config.getString("storage.address") == "null" ||
                plugin.config.getString("storage.username") == "null" ||
                plugin.config.getString("storage.password") == "null") {
                throw Exception("MySQL credentials not set in config")
            }
        }
    }

    fun migrate() {
        // Quick method to set new storage type in config
        plugin.config.set("storage.type", newStorageType.name)
        plugin.saveConfig()
        plugin.reloadConfig()

        Settings.reloadDataManager()
        dataManager = Settings.getDataManger()

        // Migrate event data
        dataManager.setEventEndTime(eventEndTime)
        dataManager.setEventMaterial(activeMaterial)
        if (Settings.usingEconomy()) dataManager.setEventSellPrice(activeSellPrice)

        // Migrate player data
        // TODO: /shrug
        for (hoarderPlayer in hoarderPlayers) {
            dataManager.addPoints(hoarderPlayer.uuid, hoarderPlayer.getPoints())
            dataManager.addClaimableTreasures(hoarderPlayer.uuid, hoarderPlayer.getClaimableTreasures())
        }

        if (treasureItems != null) {
            // Migrate treasure data
            for (treasureItem in treasureItems) {
                dataManager.addTreasureItem(treasureItem)
            }
        }
    }
}