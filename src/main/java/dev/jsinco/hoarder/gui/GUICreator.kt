package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.utilities.Util
import dev.jsinco.hoarder.gui.enums.GUIType
import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class GUICreator (path: String) : InventoryHolder {

    val file = FileManager(path).getFileYaml()
    val title: String = Util.fullColor(file.getString("title")!!)
    val size: Int = file.getInt("size")

    val gui: Inventory = Bukkit.createInventory(this, size, title)
    private val itemsList: MutableList<GUIItem> = mutableListOf()

    val guiType = GUIType.valueOf(file.getString("gui-type")?.uppercase() ?: "OTHER")

    var paginatedGUI: PaginatedGUI? = null
    var guiRunnable: Int = -1

    init {
        val itemKeyPaths = file.getConfigurationSection("items")!!.getKeys(false)
        for (itemKey in itemKeyPaths) {
            itemsList.add(GUIItem(file, itemKey))
        }

        for (guiItem in itemsList) {
            if (Settings.hideIfPageNotAvailable() && (guiItem.getAction() == "[BACK_PAGE]" || guiItem.getAction() == "[NEXT_PAGE]")) {
                continue // Don't set items with this action. Let GUIUpdate Class handle it, so we can check paginatedGUI params
            }

            if (guiItem.multiSlotted) {
                for (slot in guiItem.getSlots()) {
                    gui.setItem(slot, guiItem.getItemStack())
                }
            } else {
                gui.setItem(guiItem.getSlot(), guiItem.getItemStack())
            }
        }
    }


    override fun getInventory(): Inventory {
        return gui
    }
}