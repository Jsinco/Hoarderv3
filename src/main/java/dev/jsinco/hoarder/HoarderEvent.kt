package dev.jsinco.hoarder

import dev.jsinco.hoarder.Messages.getMsg
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.Bukkit
import org.bukkit.Material
import kotlin.random.Random

/**
 * Class for the Hoarder's event
 * Handles the event's runnable, start, and end
 * Resets the Hoarder's active material, active sell price if enabled, and end time
 */
class HoarderEvent(val plugin: Hoarder) {

    companion object {
        lateinit var activeMaterial: Material
        var activeSellPrice: Double = 0.0
        var endTime: Long = 0
        var runnable: Int = -1
    }

    /**
     * Reload the event
     */
    fun reloadHoarderEvent() {
        activeMaterial = Settings.getDataManger().getEventMaterial()
        activeSellPrice = if (Settings.usingEconomy()) Settings.getDataManger().getEventSellPrice() else 0.0
        endTime = Settings.getDataManger().getEventEndTime()

        if (runnable != -1) Bukkit.getScheduler().cancelTask(runnable)
        startEventRunnable()
    }

    /**
     * Start the event
     */
    private fun restartHoarderEvent(timerLength: Long) {
        val dM = Settings.getDataManger()

        if (timerLength <= 0) {
            dM.setEventMaterial(Material.AIR)
            dM.setEventSellPrice(0.0)
            dM.setEventEndTime(0)
            return
        }

        // set material
        activeMaterial = determineEventMaterial()
        dM.setEventMaterial(activeMaterial)

        // set sell price
        if (Settings.usingEconomy()) {
            activeSellPrice = determineEventPrice()
            dM.setEventSellPrice(activeSellPrice)
        }

        // set end time
        endTime = Util.getMsTimeFromNow(timerLength)
        dM.setEventEndTime(endTime)

        val msg = getMsg("event.start")
        if (msg.isBlank()) return
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("hoarder.notify")) {
                player.sendMessage(msg)
            }
        }
    }


    /**
     * End the event
     */
    fun endHoarderEvent() {


    }


    /**
     * Runnable for the event
     */
    private fun startEventRunnable() {
        runnable =
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (System.currentTimeMillis() >= endTime) {
                endHoarderEvent()
                restartHoarderEvent(Settings.getEventTimerLength())
                reloadHoarderEvent()
            }
        },0, Settings.getEndTimeInterval())
    }

    // Helper functions to determine event details TODO: Package and separate class?

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