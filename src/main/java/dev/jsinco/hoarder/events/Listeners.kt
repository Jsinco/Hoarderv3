package dev.jsinco.hoarder.events

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.gui.GUICreator
import dev.jsinco.hoarder.gui.enums.Action
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.persistence.PersistentDataType

class Listeners(private val plugin: Hoarder) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.inventory.holder !is GUICreator) return
        event.isCancelled = true

        val clickedItem = event.currentItem ?: return
        val actionString = clickedItem.itemMeta?.persistentDataContainer?.get(NamespacedKey(plugin, "action"), PersistentDataType.STRING) ?: return

        if (actionString == "NONE") return
        val parsedAction = Action.parseStringAction(actionString)

        parsedAction.first.executeAction(parsedAction.second, event.whoClicked as Player)
    }

    // Another listener? Yay!

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder !is GUICreator) return
        val player = event.player as Player

        var preCheck = false
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
            if (player.openInventory.topInventory.holder is GUICreator) preCheck = true
        }, 1)

        if (preCheck) return
        val guiCreator = event.inventory.holder as GUICreator

        if (guiCreator.guiRunnable != -1) {
            Bukkit.getScheduler().cancelTask(guiCreator.guiRunnable)
        }
    }
}