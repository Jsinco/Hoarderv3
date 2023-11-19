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
    val file = fileManager.getFileYaml()


    // Event data

    override fun setEventEndTime(time: Long) {
        file.set("data.endtime", time)
        fileManager.saveFileYaml()
    }

    override fun getEventEndTime(): Long {
        return file.getLong("data.endtime")
    }


    override fun setEventMaterial(material: Material) {
        file.set("data.material", material)
        fileManager.saveFileYaml()
    }

    override fun getEventMaterial(): Material {
        return Material.valueOf(file.getString("data.material") ?: "AIR")
    }

    override fun setEventSellPrice(price: Double) {
        file.set("data.sellprice", price)
        fileManager.saveFileYaml()
    }

    override fun getEventSellPrice(): Double {
        return file.getDouble("data.sellprice")
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