package dev.jsinco.hoarder.storage

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.objects.HoarderPlayer
import dev.jsinco.hoarder.objects.TreasureItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.sql.Connection

class FlatFile (val plugin: Hoarder) : DataManager {
    private val prefix = plugin.config.getString("storage.table_prefix")
    private val fileManager = FileManager("${plugin.config.getString("storage.database") ?: "data"}.yml")
    private val file = fileManager.generateYamlFile()

    // Event data

    override fun setEventEndTime(time: Long) {
        file.set("${prefix}data.main.endtime", time)
        fileManager.saveFileYaml()
    }

    override fun getEventEndTime(): Long {
        return file.getLong("${prefix}data.main.endtime")
    }


    override fun setEventMaterial(material: Material) {
        file.set("${prefix}data.main.material", material.name)
        fileManager.saveFileYaml()
    }

    override fun getEventMaterial(): Material {
        return Material.valueOf(file.getString("${prefix}data.main.material") ?: "AIR")
    }

    override fun setEventSellPrice(price: Double) {
        file.set("${prefix}data.main.sellprice", price)
        fileManager.saveFileYaml()
    }

    override fun getEventSellPrice(): Double {
        return file.getDouble("${prefix}data.main.sellprice")
    }

    // Hoarder Players

    override fun addPoints(uuid: String, amount: Int) {
        file.set("${prefix}players.$uuid.points", file.getInt("${prefix}players.$uuid.points") + amount)
        fileManager.saveFileYaml()
    }

    override fun removePoints(uuid: String, amount: Int) {
        file.set("${prefix}players.$uuid.points", file.getInt("${prefix}players.$uuid.points") - amount)
        fileManager.saveFileYaml()
    }

    override fun getPoints(uuid: String): Int {
        return file.getInt("${prefix}players.$uuid.points")
    }

    override fun setPoints(uuid: String, amount: Int) {
        file.set("${prefix}players.$uuid.points", amount)
        fileManager.saveFileYaml()
    }

    override fun addClaimableTreasures(uuid: String, amount: Int) {
        file.set("${prefix}players.$uuid.claimabletreasures", file.getInt("${prefix}players.$uuid.claimabletreasures") + amount)
        fileManager.saveFileYaml()
    }

    override fun removeClaimableTreasures(uuid: String, amount: Int) {
        file.set("${prefix}players.$uuid.claimabletreasures", file.getInt("${prefix}players.$uuid.claimabletreasures") - amount)
        fileManager.saveFileYaml()
    }

    override fun getClaimableTreasures(uuid: String): Int {
        return file.getInt("${prefix}players.$uuid.claimabletreasures")
    }

    fun setClaimableTreasures(uuid: String, amount: Int) {
        file.set("${prefix}players.$uuid.claimabletreasures", amount)
        fileManager.saveFileYaml()
    }

    // Event necessities

    override fun resetAllPoints() {
        for (uuid in getAllHoarderPlayersUUIDS()) {
            file.set("${prefix}players.$uuid.points", 0)
        }
        fileManager.saveFileYaml()
    }

    override fun getEventPlayers(): Map<String, Int> {
        val eventPlayers: MutableMap<String, Int> = mutableMapOf()
        for (uuid in getAllHoarderPlayersUUIDS()) {
            val points = file.getInt("${prefix}players.$uuid.points")
            if (points != 0) {
                eventPlayers[uuid] = points
            }
        }
        return eventPlayers
    }


    override fun getAllHoarderPlayersUUIDS(): List<String> {
        return file.getConfigurationSection("${prefix}players")?.getKeys(false)?.toList() ?: emptyList()
    }

    override fun getAllHoarderPlayers(): List<HoarderPlayer> {
        val hoarderPlayers: MutableList<HoarderPlayer> = mutableListOf()
        for (uuid in getAllHoarderPlayersUUIDS()) {
            hoarderPlayers.add(HoarderPlayer(uuid))
        }
        return hoarderPlayers
    }

    // Treasure Items

    override fun addTreasureItem(treasureItem: TreasureItem) {
        addTreasureItem(treasureItem.identifier, treasureItem.weight, treasureItem.itemStack)
    }

    override fun modifyTreasureItem(identifier: String, newWeight: Int, newIdentifier: String) {
        val itemStack = file.getItemStack("${prefix}treasure_items.$identifier.itemstack") ?: return
        file.set("${prefix}treasure_items.$identifier", null)
        file.set("${prefix}treasure_items.$newIdentifier.weight", newWeight)
        file.set("${prefix}treasure_items.$newIdentifier.itemstack", itemStack)
        fileManager.saveFileYaml()
    }

    override fun addTreasureItem(identifier: String, weight: Int, itemStack: ItemStack) {
        if (file.get("${prefix}treasure_items.$identifier") != null) return

        file.set("${prefix}treasure_items.$identifier.weight", weight)
        file.set("${prefix}treasure_items.$identifier.itemstack", itemStack)
        fileManager.saveFileYaml()
    }

    override fun removeTreasureItem(identifier: String) {
        file.set("${prefix}treasure_items.$identifier", null)
        fileManager.saveFileYaml()
    }

    override fun getTreasureItem(identifier: String): TreasureItem {
        return TreasureItem(identifier, file.getInt("${prefix}treasure_items.$identifier.weight"), file.getItemStack("${prefix}treasure_items.$identifier.itemstack")!!)
    }


    override fun getAllTreasureItems(): List<TreasureItem> {
        val treasureItems: MutableList<TreasureItem> = mutableListOf()

        for (key in file.getConfigurationSection("${prefix}treasure_items")?.getKeys(false) ?: return emptyList()) {
            treasureItems.add(
                TreasureItem(key, file.getInt("${prefix}treasure_items.$key.weight"), file.getItemStack("${prefix}treasure_items.$key.itemstack")!!)
            )
        }
        return treasureItems
    }

    override fun addMsgQueuedPlayer(uuid: String, position: Int) {
        file.set("${prefix}cache.$uuid.position", position)
        fileManager.saveFileYaml()
    }

    override fun removeMsgQueuedPlayer(uuid: String) {
        file.set("${prefix}cache.$uuid", null)
        fileManager.saveFileYaml()
    }

    override fun isMsgQueuedPlayer(uuid: String): Boolean {
        return file.getConfigurationSection("${prefix}cache")?.getKeys(false)?.contains(uuid) ?: false
    }

    override fun getMsgQueuedPlayerPosition(uuid: String): Int {
        return file.getInt("${prefix}cache.$uuid.position")
    }

    // SQL / File

    override fun getSQLConnection(): Connection {
        throw UnsupportedOperationException("Flatfile does not support SQL! This method is meant for database calls!")
    }

    override fun closeConnection() {
        throw UnsupportedOperationException("Flatfile does not support SQL! This method is meant for database calls!")
    }

    override fun getFile(): FileManager {
        return fileManager
    }

    override fun saveFile() {
        fileManager.saveFileYaml()
    }
}