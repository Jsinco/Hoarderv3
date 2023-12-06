package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.utilities.Util
import dev.jsinco.hoarder.utilities.Util.replaceTopPlaceholders
import dev.jsinco.hoarder.manager.FileManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

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
        val item = ItemStack(Material.valueOf(file.getString("items.$key.material")!!.uppercase()))
        val meta = item.itemMeta!!

        val name = file.getString("items.$key.name")!!
        val lore = file.getStringList("items.$key.lore")
        val data: Comparable<*>? = if (file.get("items.$key.data") is Int) file.getInt("items.$key.data") else file.getString("items.$key.data")


        if (file.get("items.$key.name") != null) meta.setDisplayName(Util.fullColor(name))
        if (file.get("items.$key.lore") != null) meta.lore = Util.fullColor(lore)
        if (file.getBoolean("items.$key.enchanted")) meta.addEnchant(Enchantment.DURABILITY, 1, true)
        if (data != null && data is Int) meta.setCustomModelData(data)



        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
        meta.persistentDataContainer.set(NamespacedKey(plugin, "action"), PersistentDataType.STRING, file.getString("items.$key.action") ?: "NONE")

        item.itemMeta = meta

        if (name.contains("%top_") || lore.toString().contains("%top_") || (data != null && data is String && data.contains("%top_"))) {
            return setTopPlayerItemPlaceholders(item, data as String)
        }
        return item
    }

    // FIXME
    private fun setTopPlayerItemPlaceholders(itemStack: ItemStack, data: String): ItemStack {
        var item = itemStack
        val meta = item.itemMeta!!
        val eventPlayers = Util.getEventPlayersByTop()
        val dmFile = FileManager("guis/dynamicitems.yml").getFileYaml()

        meta.setDisplayName(Util.fullColor(replaceTopPlaceholders(meta.displayName, eventPlayers) ?: dmFile.getString("items.empty_position.name")!!))
        var wasNull = false
        meta.lore = meta.lore!!.map { Util.fullColor(replaceTopPlaceholders(it, eventPlayers) ?:  run { wasNull = true; "" }) }
        if (wasNull) meta.lore = Util.fullColor(dmFile.getStringList("items.empty_position.lore"))

        val uuid = replaceTopPlaceholders(data, eventPlayers) ?: run { item.type = Material.valueOf(dmFile.getString("items.empty_position.material")!!); "" }
        item.itemMeta = meta
        item = setPlayerHead(itemStack, uuid)
        return item
    }

    companion object {
        fun setPlayerHead(itemStack: ItemStack, uuid: String): ItemStack {
            if (itemStack.type != Material.PLAYER_HEAD) return itemStack
            val meta: SkullMeta = itemStack.itemMeta as SkullMeta
            meta.owningPlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid))
            itemStack.itemMeta = meta
            return itemStack
        }
    }
}