package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.FileManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class GUICreator (
    val path: String,
    val guiType: GUIType
) : InventoryHolder {

    val file = FileManager(path).getFileYaml()

    val title: String = file.getString("title")!!
    val size: Int = file.getInt("size")

    val gui: Inventory = Bukkit.createInventory(this, size, title)


    private val itemsList: MutableList<GUIItem> = mutableListOf()

    init {
        val itemKeyPaths = file.getConfigurationSection("items")!!.getKeys(false)
        for (itemKey in itemKeyPaths) {
            itemsList.add(GUIItem(file, itemKey))
        }
    }

    override fun getInventory(): Inventory {
        TODO("Not yet implemented")
    }

}

private class GUIItem (val file: YamlConfiguration, val key: String) {

    fun getSlot(): Int {
        return file.getInt("items.$key.slot")
    }

    fun getAction(): String? {
        return file.getString("items.$key.action")
    }

    fun getItemStack(): ItemStack {
        val item = ItemStack(Material.valueOf("items.$key.material"))
        val meta = item.itemMeta!!

        meta.setDisplayName(file.getString("items.$key.name"))
        meta.lore = file.getStringList("items.$key.lore")
        if (file.getBoolean("items.$key.enchanted")) meta.addEnchant(Enchantment.DURABILITY, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)

        item.itemMeta = meta
        return item
    }
}