package dev.jsinco.hoarder.gui

import com.iridium.iridiumcolorapi.IridiumColorAPI
import dev.jsinco.hoarder.FileManager
import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.Util
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

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
            gui.setItem(guiItem.getSlot(), guiItem.getItemStack())
        }
    }

    override fun getInventory(): Inventory {
        return gui
    }

}

private class GUIItem (val file: YamlConfiguration, val key: String) {

    private val plugin: Hoarder = Hoarder.getInstance()

    fun getSlot(): Int {
        return file.getInt("items.$key.slot")
    }

    fun getAction(): String {
        return file.getString("items.$key.action") ?: "NONE"
    }

    fun getItemStack(): ItemStack {
        val item = ItemStack(Material.valueOf(file.getString("items.$key.material")!!))
        val meta = item.itemMeta!!

        meta.setDisplayName(Util.fullColor(file.getString("items.$key.name")!!))
        meta.lore = Util.fullColor(file.getStringList("items.$key.lore"))
        if (file.getBoolean("items.$key.enchanted")) meta.addEnchant(Enchantment.DURABILITY, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
        meta.persistentDataContainer.set(NamespacedKey(plugin, "action"), PersistentDataType.STRING, file.getString("items.$key.action") ?: "NONE")

        item.itemMeta = meta
        return item
    }
}