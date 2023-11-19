package dev.jsinco.hoarder.manager

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.storage.DataManager
import dev.jsinco.hoarder.storage.FlatFile
import dev.jsinco.hoarder.storage.StorageType
import dev.jsinco.hoarder.storage.sql.MySQL
import dev.jsinco.hoarder.storage.sql.SQLite


object Settings {

    val plugin: Hoarder = Hoarder.getInstance()
    private var dataManager: DataManager? = null

    @JvmStatic
    fun getEventEndTime(): Long {
        return plugin.config.getLong("event.timer-length")
    }

    @JvmStatic
    fun getMaxWinnerAmount(): Int {
        return plugin.config.getConfigurationSection("event.winners")?.getKeys(false)?.size ?: 0
    }

    @JvmStatic
    fun getTreasureAmountForWinnerSpot(spot: Int): Int {
        return plugin.config.getInt("event.winners.$spot")
    }

    @JvmStatic
    fun getStorageType(): StorageType {
        return StorageType.valueOf(plugin.config.getString("storage.type")?.uppercase() ?: "SQLITE")
    }



    @JvmStatic
    fun getDataManger(): DataManager {
        if (dataManager == null) {
            reloadDataManager()
        }
        return dataManager!!
    }

    fun reloadDataManager() {
        if (dataManager != null && (dataManager is MySQL || dataManager is SQLite)) {
            dataManager?.closeConnection()
        }

        dataManager = when (getStorageType()) {
            StorageType.MYSQL -> MySQL(plugin)
            StorageType.SQLITE -> SQLite(plugin)
            StorageType.FLATFILE -> FlatFile(plugin)
        }
        plugin.logger.info("Loading ${getStorageType()} as storage type...")
    }
}