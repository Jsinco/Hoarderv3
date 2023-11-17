package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.Util
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class GUIItem (val file: YamlConfiguration, val key: String) {

    private val plugin: Hoarder = Hoarder.getInstance()
    val multiSlotted: Boolean = file.getIntegerList("items.$key.slot").isNotEmpty()

    fun getSlots(): List<Int> {
        return file.getIntegerList("items.$key.slot")
    }

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