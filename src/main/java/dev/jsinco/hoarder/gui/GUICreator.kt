package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.FileManager
import dev.jsinco.hoarder.Util
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class GUICreator (
    val path: String
) : InventoryHolder {

    val file = FileManager(path).getFileYaml()
    val title: String = Util.fullColor(file.getString("title")!!)
    val size: Int = file.getInt("size")
    private val gui: Inventory = Bukkit.createInventory(this, size, title)
    private val itemsList: MutableList<GUIItem> = mutableListOf()

    init {
        val itemKeyPaths = file.getConfigurationSection("items")!!.getKeys(false)
        for (itemKey in itemKeyPaths) {
            itemsList.add(GUIItem(file, itemKey))
        }

        for (guiItem in itemsList) {
            if (guiItem.multiSlotted) {
                for (i in 0..guiItem.getSlots().size) {
                    gui.setItem(guiItem.getSlots()[i], guiItem.getItemStack())
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