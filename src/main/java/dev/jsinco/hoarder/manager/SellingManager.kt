package dev.jsinco.hoarder.manager

import dev.jsinco.hoarder.economy.EconomyHandler
import dev.jsinco.hoarder.economy.PlayerPointsHook
import dev.jsinco.hoarder.economy.ProviderType
import dev.jsinco.hoarder.economy.VaultHook
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class SellingManager(val player: Player, val inventory: Inventory) {



    var amountSold = 0
    var payoutAmount = 0.0

    fun sellActiveItem() {
        val activeMaterial = Settings.getDataManger().getEventMaterial()

        for (item in inventory.contents) {
            if (item != null && item.type == activeMaterial) {
                amountSold += item.amount
                item.amount = 0
            }
        }
        Settings.getDataManger().addPoints(player.uniqueId.toString(), amountSold)
    }

    fun payoutPlayer() {
        val usingEcon = Settings.usingEconomy()

        if (!usingEcon || amountSold == 0) return
        val sellPrice = Settings.getDataManger().getEventSellPrice()

        payoutAmount =  sellPrice * amountSold

        val econHandler: EconomyHandler = when (Settings.getEconomyProvider()) {
            ProviderType.VAULT -> VaultHook()
            ProviderType.PLAYERPOINTS -> PlayerPointsHook()
        }

        econHandler.addBalance(payoutAmount, player)
    }
}