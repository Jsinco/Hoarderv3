package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory

/**
 * Class intended to update gui items that do not update constantly off a runnable
 */
class GUIUpdater (guiCreator: GUICreator) {

    companion object{
        private val plugin: Hoarder = Hoarder.getInstance()
    }

    val file = guiCreator.file
    val gui: Inventory = guiCreator.gui

    init {
        val itemsList: MutableList<GUIItem> = mutableListOf()

        val itemKeyPaths = file.getConfigurationSection("items")!!.getKeys(false)
        for (itemKey in itemKeyPaths) {
            itemsList.add(GUIItem(file, itemKey))
        }

        val guis = mutableListOf<Inventory>()

        if (guiCreator.paginatedGUI != null) {
            guis.addAll(guiCreator.paginatedGUI!!.pages)
        } else {
            guis.add(gui)
        }

        for ((index, inv) in guis.withIndex()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { // Async
                for (guiItem in itemsList) {
                    if (Settings.hideIfPageNotAvailable() && guiCreator.paginatedGUI != null && (index == 0 || index == guis.size - 1)) {
                        if (guiItem.getAction() == "[BACK_PAGE]" && index == 0) continue
                        if (guiItem.getAction() == "[NEXT_PAGE]" && index == guis.size - 1) continue
                    }

                    if (guiItem.multiSlotted) {
                        for (slot in guiItem.getSlots()) {
                            inv.setItem(slot, guiItem.getItemStack())
                        }
                    } else {
                        inv.setItem(guiItem.getSlot(), guiItem.getItemStack())
                    }
                }
            })
        }
    }

    /*
    companion object {
        fun hideItemsIfPageNotAvailable(guiCreator: GUICreator) {
            val paginatedGUI = guiCreator.paginatedGUI ?: return


            paginatedGUI.getPage(0)
        }
    }
     */
}