package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.Util
import dev.jsinco.hoarder.gui.enums.GUIType
import dev.jsinco.hoarder.manager.FileManager
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class GUICreator (val path: String) : InventoryHolder {

    private val plugin: Hoarder = Hoarder.getInstance()

    val file = FileManager(path).getFileYaml()
    val title: String = Util.fullColor(file.getString("title")!!)
    val size: Int = file.getInt("size")

    val gui: Inventory = Bukkit.createInventory(this, size, title)
    val itemsList: MutableList<GUIItem> = mutableListOf()

    val guiType = GUIType.valueOf(file.getString("gui-type")?.uppercase() ?: "OTHER")

    init {
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


    override fun getInventory(): Inventory {
        return gui
    }

    // Hopefully no exceptions
    var paginatedGUI: PaginatedGUI? = null
}