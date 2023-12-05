package dev.jsinco.hoarder.objects

import org.bukkit.inventory.ItemStack

/**
 * Represents an item that can be found in treasure
 * @param identifier The identifier of the item
 * @param weight The weight of the item
 * @param itemStack The itemStack of the item
 */
data class TreasureItem (
    val identifier: String,
    val weight: Int,
    val itemStack: ItemStack
)