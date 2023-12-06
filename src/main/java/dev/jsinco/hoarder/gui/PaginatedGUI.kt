package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.utilities.Util
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class PaginatedGUI (
    val name: String,
    private val base: Inventory,
    items: List<ItemStack?>
) {

    val pages: MutableList<Inventory> = mutableListOf()
    val isEmpty = items.isEmpty()
    var size : Int = 0
        private set


    init {
        var currentPage = newPage()
        var currentItem = 0
        var currentSlot = 0
        while (currentItem < items.size) {
            if (currentSlot == currentPage.size) {
                currentPage = newPage()
                currentSlot = 0
            }

            if (currentPage.getItem(currentSlot) == null) {
                currentPage.setItem(currentSlot, items[currentItem])
                currentItem++
            }
            currentSlot++
        }
        size = pages.size
    }

    private fun newPage(): Inventory {
        val inventory: Inventory = Bukkit.createInventory(base.holder, base.size, Util.fullColor(name))
        for (i in 0 until base.size) {
            inventory.setItem(i, base.getItem(i))
        }
        pages.add(inventory)
        return inventory
    }


    fun getPage(page: Int): Inventory {
        return pages[page]
    }

    fun indexOf(page: Inventory): Int {
        return pages.indexOf(page)
    }
}