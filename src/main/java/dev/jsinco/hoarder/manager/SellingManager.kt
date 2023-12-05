package dev.jsinco.hoarder.manager

import dev.jsinco.hoarder.HoarderEvent
import dev.jsinco.hoarder.api.HoarderSellEvent
import dev.jsinco.hoarder.economy.EconomyHandler
import dev.jsinco.hoarder.economy.PlayerPointsHook
import dev.jsinco.hoarder.economy.ProviderType
import dev.jsinco.hoarder.economy.VaultHook
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class SellingManager(val player: Player, val inventory: Inventory) {

    var amountSold = 0
    var payoutAmount = 0.0

    fun sellActiveItem() {
        val activeMaterial = HoarderEvent.activeMaterial

        for (item in inventory.contents) {
            if (item != null && item.type == activeMaterial) {
                amountSold += item.amount
                item.amount = 0
            }
        }
        Settings.getDataManger().addPoints(player.uniqueId.toString(), amountSold)

        val usingEcon = Settings.usingEconomy()

        if (!usingEcon || amountSold == 0) return
        val sellPrice = HoarderEvent.activeSellPrice

        payoutAmount = sellPrice * amountSold

        val econHandler: EconomyHandler = when (Settings.getEconomyProvider()) {
            ProviderType.VAULT -> VaultHook()
            ProviderType.PLAYERPOINTS -> PlayerPointsHook()
        }

        econHandler.addBalance(payoutAmount, player)

        Bukkit.getPluginManager().callEvent(HoarderSellEvent(player, amountSold, sellPrice))
    }
}