package dev.jsinco.hoarder.storage

import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.objects.HoarderPlayer
import dev.jsinco.hoarder.objects.TreasureItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.sql.Connection
import java.util.UUID

interface DataManager {

    // Event data

    /**
     * @param time The end time of the event in milliseconds
     */
    fun setEventEndTime(time: Long)

    /**
     * @return The end time of the event in milliseconds
     */
    fun getEventEndTime(): Long

    /**
     * @param material Sets the current event material
     */
    fun setEventMaterial(material: Material)


    /**
     * @return The current event material
     */
    fun getEventMaterial(): Material


    /**
     * @param price sets the current event sell price
     */
    fun setEventSellPrice(price: Double)


    /**
     * @return The current event sell price
     */
    fun getEventSellPrice(): Double

    // Hoarder Players
    // TODO: Label these functions

    fun addPoints(uuid: String, amount: Int)

    fun removePoints(uuid: String, amount: Int)

    fun getPoints(uuid: String): Int

    fun setPoints(uuid: String, amount: Int)

    fun addClaimableTreasures(uuid: String, amount: Int)

    fun removeClaimableTreasures(uuid: String, amount: Int)

    fun getClaimableTreasures(uuid: String): Int


    fun resetAllPoints()

    fun getEventPlayers(): Map<String, Int>

    /**
     * @return A list of all HoarderPlayer objects
     */
    fun getAllHoarderPlayersUUIDS(): List<String>

    fun getAllHoarderPlayers(): List<HoarderPlayer>


    // Treasure items

    /**
     * @param treasureItem The TreasureItem object to add or update to the database
     */
    fun addTreasureItem(treasureItem: TreasureItem)

    /**
     * @param identifier The identifier of the TreasureItem to remove from the database
     * @param weight The weight of the TreasureItem to remove from the database
     * @param itemStack The itemStack of the TreasureItem to remove from the database
     */
    fun addTreasureItem(identifier: String, weight: Int, itemStack: ItemStack)

    fun modifyTreasureItem(identifier: String, newWeight: Int, newIdentifier: String)

    /**
     * @param identifier The identifier of the TreasureItem to remove from the database
     */
    fun removeTreasureItem(identifier: String)

    /**
     * @param identifier The identifier of the TreasureItem to get
     */
    fun getTreasureItem(identifier: String): TreasureItem?

    /**
     * @return A list of all TreasureItem objects
     */
    fun getAllTreasureItems(): List<TreasureItem>?


    // Message Queued Players
    fun addMsgQueuedPlayer(uuid: String, position: Int)

    fun removeMsgQueuedPlayer(uuid: String)

    fun isMsgQueuedPlayer(uuid: String): Boolean

    fun getMsgQueuedPlayerPosition(uuid: String): Int

    // SQL / File

    fun getSQLConnection(): Connection

    fun getFile(): FileManager

    fun closeConnection()

    fun saveFile()
}