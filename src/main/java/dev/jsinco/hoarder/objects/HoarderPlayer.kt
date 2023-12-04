package dev.jsinco.hoarder.objects

import dev.jsinco.hoarder.Util
import dev.jsinco.hoarder.storage.DataManager
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.random.Random

/**
 * Hoarder representation of a player that can interact with the database
 * @param uuid The UUID of the player
 */
class HoarderPlayer (val uuid: String) {

    companion object {
        val dataManager: DataManager = Settings.getDataManger()
    }

    private var points: Int = dataManager.getPoints(uuid)
    private var claimableTreasures: Int = dataManager.getClaimableTreasures(uuid)

    fun addPoints(amount: Int) {
        dataManager.addPoints(uuid, amount)
        points += amount
    }

    fun removePoints(amount: Int) {
        dataManager.removePoints(uuid, amount)
        points -= amount
    }

    fun getPoints(): Int {
        return points
    }


    fun addClaimableTreasures(amount: Int) {
        dataManager.addClaimableTreasures(uuid, amount)
        claimableTreasures += amount
    }

    fun removeClaimableTreasures(amount: Int) {
        dataManager.removeClaimableTreasures(uuid, amount)
        claimableTreasures -= amount
    }

    fun getClaimableTreasures(): Int {
        return claimableTreasures
    }

    // Etc

    fun getPlayer(): Player? {
        val offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid))
        return if (offlinePlayer.isOnline) offlinePlayer.player else null
    }

    fun getOfflinePlayer(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid))
    }

    fun getName(): String {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).name ?: "Unknown"
    }

    fun claimReward() {
        val player = getPlayer() ?: return
        val claimable = getClaimableTreasures()
        if (claimable <= 0) return

        val treasures = dataManager.getAllTreasureItems() ?: return

        var item: ItemStack? = null
        while (item == null) {
            val random = Random.nextInt(Settings.treasureBoundInt())

            val treasureItem = treasures.random()

            if (treasureItem.weight <= random) {
                item = treasureItem.itemStack
            }
        }
        player.inventory.addItem(item)


    }
}