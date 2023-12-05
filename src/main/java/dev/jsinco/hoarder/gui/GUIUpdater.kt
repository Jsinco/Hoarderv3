package dev.jsinco.hoarder.gui

import org.bukkit.inventory.Inventory

/**
 * Class intended to update gui items that do not update constantly off a runnable
 */
class GUIUpdater (guiCreator: GUICreator) {

    val file = guiCreator.file
    val gui: Inventory = guiCreator.gui

    init {
        val itemsList: MutableList<GUIItem> = mutableListOf()

        val itemKeyPaths = file.getConfigurationSection("items")!!.getKeys(false)
        for (itemKey in itemKeyPaths) {
            itemsList.add(GUIItem(file, itemKey))
        }

        for (guiItem in itemsList) {
            if (guiItem.multiSlotted) {
                for (slot in guiItem.getSlots()) {
                    gui.setItem(slot, guiItem.getItemStack())
                }
            } else {
                gui.setItem(guiItem.getSlot(), guiItem.getItemStack())
            }
        }
    }
}