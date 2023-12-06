package dev.jsinco.hoarder.manager

import dev.jsinco.hoarder.HoarderEvent
import dev.jsinco.hoarder.api.HoarderSellEvent
import dev.jsinco.hoarder.economy.EconomyHandler
import dev.jsinco.hoarder.economy.PlayerPointsHook
import dev.jsinco.hoarder.economy.ProviderType
import dev.jsinco.hoarder.economy.VaultHook
import dev.jsinco.hoarder.gui.GUICreator
import dev.jsinco.hoarder.gui.GUIUpdater
import dev.jsinco.hoarder.utilities.Messages.getPrefix
import dev.jsinco.hoarder.utilities.Messages.messagesFile
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class SellingManager(val player: Player, val inventory: Inventory) {

    var amountSold = 0
    var payoutAmount = 0.0
    var payoutString = "No Econ"

    fun sellActiveItem() {
        val activeMaterial = HoarderEvent.activeMaterial

        for (item in inventory.contents) {
            if (item != null && item.type == activeMaterial) {
                amountSold += item.amount
                item.amount = 0
            }
        }
        Settings.getDataManger().addPoints(player.uniqueId.toString(), amountSold)

        var sellPrice: Double = 0.0

        if (Settings.usingEconomy() && amountSold > 0) {
            sellPrice = HoarderEvent.activeSellPrice

            payoutAmount = sellPrice * amountSold

            val econHandler: EconomyHandler = when (Settings.getEconomyProvider()) {
                ProviderType.VAULT -> VaultHook()
                ProviderType.PLAYERPOINTS -> PlayerPointsHook()
            }

            payoutString = econHandler.addBalance(payoutAmount, player).toString()
        }


        Bukkit.getPluginManager().callEvent(HoarderSellEvent(player, amountSold, sellPrice))


        var msg = if (amountSold > 0) messagesFile.getString("actions.sell") else messagesFile.getString("actions.sell-none")
        if (msg != null) {
            msg = msg.replace("%amount%", amountSold.toString())
                .replace("%payout%", payoutString)
        }

        player.sendMessage(getPrefix() + msg)
    }
}