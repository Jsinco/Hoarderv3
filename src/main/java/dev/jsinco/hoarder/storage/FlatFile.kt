package dev.jsinco.hoarder.storage

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.objects.HoarderPlayer
import dev.jsinco.hoarder.objects.TreasureItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.sql.Connection

class FlatFile (val plugin: Hoarder) : DataManager {
    val fileManager = FileManager("${plugin.config.getString("storage.database") ?: "data"}.yml")
    val file = fileManager.generateYamlFile()



    // Event data

    override fun setEventEndTime(time: Long) {
        file.set("data.main.endtime", time)
        fileManager.saveFileYaml()
    }

    override fun getEventEndTime(): Long {
        return file.getLong("data.main.endtime")
    }


    override fun setEventMaterial(material: Material) {
        file.set("data.main.material", material.name)
        fileManager.saveFileYaml()
    }

    override fun getEventMaterial(): Material {
        return Material.valueOf(file.getString("data.main.material") ?: "AIR")
    }

    override fun setEventSellPrice(price: Double) {
        file.set("data.main.sellprice", price)
        fileManager.saveFileYaml()
    }

    override fun getEventSellPrice(): Double {
        return file.getDouble("data.main.sellprice")
    }

    // Hoarder Players

    override fun addPoints(uuid: String, amount: Int) {
        file.set("players.$uuid.points", file.getInt("players.$uuid.points") + amount)
        fileManager.saveFileYaml()
    }

    override fun removePoints(uuid: String, amount: Int) {
        file.set("players.$uuid.points", file.getInt("players.$uuid.points") - amount)
        fileManager.saveFileYaml()
    }

    override fun getPoints(uuid: String): Int {
        return file.getInt("players.$uuid.points")
    }

    override fun addClaimableTreasures(uuid: String, amount: Int) {
        file.set("players.$uuid.claimabletreasures", file.getInt("players.$uuid.claimabletreasures") + amount)
        fileManager.saveFileYaml()
    }

    override fun removeClaimableTreasures(uuid: String, amount: Int) {
        file.set("players.$uuid.claimabletreasures", file.getInt("players.$uuid.claimabletreasures") - amount)
        fileManager.saveFileYaml()
    }

    override fun getClaimableTreasures(uuid: String): Int {
        return file.getInt("players.$uuid.claimabletreasures")
    }

    // Event necessities

    override fun resetAllPoints() {
        for (uuid in getAllHoarderPlayersUUIDS()) {
            file.set("players.$uuid.points", 0)
        }
        fileManager.saveFileYaml()
    }

    override fun getEventPlayers(): Map<String, Int> {
        val eventPlayers: MutableMap<String, Int> = mutableMapOf()
        for (uuid in getAllHoarderPlayersUUIDS()) {
            val points = file.getInt("players.$uuid.points")
            if (points != 0) {
                eventPlayers[uuid] = points
            }
        }
        return eventPlayers
    }


    override fun getAllHoarderPlayersUUIDS(): List<String> {
        return file.getConfigurationSection("players")?.getKeys(false)?.toList() ?: emptyList()
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
        println(file.root)
        file.set("treasure_items.${treasureItem.identifier}.weight", treasureItem.weight)
        file.set("treasure_items.${treasureItem.identifier}.itemstack", treasureItem.itemStack)
        fileManager.saveFileYaml()
    }

    override fun modifyTreasureItem(identifier: String, newWeight: Int, newIdentifier: String) {
        val itemStack = file.getItemStack("treasure_items.$identifier.itemstack") ?: return
        file.set("treasure_items.$identifier", null)
        file.set("treasure_items.$newIdentifier.weight", newWeight)
        file.set("treasure_items.$newIdentifier.itemstack", itemStack)
        fileManager.saveFileYaml()
    }

    override fun addTreasureItem(identifier: String, weight: Int, itemStack: ItemStack) {
        file.set("treasure_items.$identifier.weight", weight)
        file.set("treasure_items.$identifier.itemstack", itemStack)
        fileManager.saveFileYaml()
    }

    override fun removeTreasureItem(identifier: String) {
        file.set("treasure_items.$identifier", null)
        fileManager.saveFileYaml()
    }

    override fun getTreasureItem(identifier: String): TreasureItem {
        return TreasureItem(identifier, file.getInt("treasure_items.$identifier.weight"), file.getItemStack("treasure_items.$identifier.itemstack")!!)
    }


    override fun getAllTreasureItems(): List<TreasureItem> {
        val treasureItems: MutableList<TreasureItem> = mutableListOf()

        for (key in file.getConfigurationSection("treasure_items")?.getKeys(false) ?: return emptyList()) {
            treasureItems.add(
                TreasureItem(key, file.getInt("treasure_items.$key.weight"), file.getItemStack("treasure_items.$key.itemstack")!!)
            )
        }
        return treasureItems
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