package dev.jsinco.hoarder.manager

import dev.jsinco.hoarder.objects.HoarderPlayer
import org.bukkit.Material
import org.bukkit.inventory.Inventory

class SellingManager(val hoarderPlayer: HoarderPlayer, val inventory: Inventory) {

    private val usingEcon = Settings.usingEconomy()
    private val activeMaterial = Settings.getDataManger().getEventMaterial()
    var amountSold = 0

    fun sellActiveItem() {
        for (item in inventory.contents) {
            if (item != null && item.type == activeMaterial) {
                amountSold += item.amount
                item.type = Material.AIR
            }
        }
    }
}