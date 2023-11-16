package dev.jsinco.hoarder.gui

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class GUIHolder(
    val guiType: GUIType
) : InventoryHolder {

    override fun getInventory(): Inventory {
        TODO("Not yet implemented")
    }

}