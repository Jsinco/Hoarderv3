package dev.jsinco.hoarder

import dev.jsinco.hoarder.api.HoarderEndEvent
import dev.jsinco.hoarder.api.HoarderStartEvent
import dev.jsinco.hoarder.manager.SellingManager
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.objects.LangMsg
import dev.jsinco.hoarder.utilities.Util
import org.bukkit.Bukkit
import org.bukkit.Material
import java.util.*
import kotlin.random.Random

/**
 * Static Class for the Hoarder's event
 * Handles the event's runnable, start, and end
 * Resets the Hoarder's active material, active sell price if enabled, and end time
 */
object HoarderEvent {
    private val plugin = Hoarder.getInstance()

    lateinit var activeMaterial: Material
    var activeSellPrice: Double = 0.0
    var endTime: Long = 0
    var runnable: Int = -1

    private var dataManager = Settings.getDataManger()

    /**
     * Reload the event
     */
    fun reloadHoarderEvent() {
        activeMaterial = dataManager.getEventMaterial()
        activeSellPrice = if (Settings.usingEconomy()) dataManager.getEventSellPrice() else 0.0
        endTime = dataManager.getEventEndTime()

        if (runnable != -1) Bukkit.getScheduler().cancelTask(runnable)
        runnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (System.currentTimeMillis() < endTime) return@scheduleSyncRepeatingTask

            dataManager = Settings.getDataManger() // Ensure dataManager is up-to-date

            val hoarderEndEvent = HoarderEndEvent() // API
            val hoarderStartEvent = HoarderStartEvent()
            Bukkit.getPluginManager().callEvent(hoarderEndEvent)
            Bukkit.getPluginManager().callEvent(hoarderStartEvent)

            if (!hoarderEndEvent.isCancelled){
                endHoarderEvent()
            }
            if (!hoarderStartEvent.isCancelled) {
                startHoarderEvent(Settings.getEventTimerLength())
            }
            //reloadHoarderEvent()
        },0, Settings.getEndTimeInterval())
    }


    /**
     * Start the event
     */
    fun startHoarderEvent(timerLength: Long) {


        if (timerLength <= 0) {
            dataManager.setEventMaterial(Material.AIR)
            dataManager.setEventSellPrice(0.0)
            dataManager.setEventEndTime(0)
            return
        }

        // set material
        activeMaterial = determineEventMaterial()
        dataManager.setEventMaterial(activeMaterial)

        // set sell price
        if (Settings.usingEconomy()) {
            activeSellPrice = determineEventPrice()
            dataManager.setEventSellPrice(activeSellPrice)
        }

        // set end time
        endTime = System.currentTimeMillis() + (timerLength * 60000)
        dataManager.setEventEndTime(endTime)
    }


    /**
     * End the event
     */
    fun endHoarderEvent() {
        if (SellingManager.locked) SellingManager.locked = false

        val winnerPositions = Settings.getWinners()
        val eventPlayers = Util.getEventPlayersByTop().keys.toList()

        for (position in winnerPositions.keys) {
            if (eventPlayers.size < position) break
            val uuid = eventPlayers[position - 1]

            dataManager.addClaimableTreasures(uuid, winnerPositions[position]!!)
            if (!Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline) {
                dataManager.addMsgQueuedPlayer(uuid, position)
            }
        }


        val eventPlayersMap = Util.getEventPlayersByTop()

        val msg = LangMsg("notifications.hoarder-event-end").getMsgListSendSound(Bukkit.getOnlinePlayers().toList()).map{
            Util.replaceTopPlayerPlaceholders(it, eventPlayersMap) ?: (LangMsg.prefix + LangMsg("actions.empty-position").message)
        }

        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("hoarder.notify")) continue
            for (message in msg) {
                player.sendMessage(message)
            }
        }
        dataManager.resetAllPoints()
    }


    private fun determineEventMaterial(): Material {
        val materials: MutableList<Material> = mutableListOf()
        if (Settings.useWhiteListAsBlackList()) {
            for (material in Material.entries) {
                if (material.isItem && !Settings.getAllMaterials().contains(material)) {
                    materials.add(material)
                }
            }
        } else {
            materials.addAll(Settings.getAllMaterials())
        }

        return materials.random()
    }

    private fun determineEventPrice(): Double {
        if (Settings.useRandomPricing()) {
            val bounds = Settings.randomPricingBounds()
            return Random.nextDouble(bounds.first, bounds.second)
        }
        return Settings.getMaterialPrice(activeMaterial)
    }
}