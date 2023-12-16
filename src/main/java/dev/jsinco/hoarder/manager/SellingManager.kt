package dev.jsinco.hoarder.manager

import dev.jsinco.hoarder.HoarderEvent
import dev.jsinco.hoarder.api.HoarderSellEvent
import dev.jsinco.hoarder.economy.EconomyHandler
import dev.jsinco.hoarder.economy.PlayerPointsHook
import dev.jsinco.hoarder.economy.ProviderType
import dev.jsinco.hoarder.economy.VaultHook
import dev.jsinco.hoarder.objects.LangMsg
import dev.jsinco.hoarder.utilities.Util
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class SellingManager(val player: Player, val inventory: Inventory) {

    companion object {
        var locked: Boolean = false
    }

    private var amountSold = 0
    private var payoutAmount = 0.0
    private var payoutString = "No Econ"

    fun sellActiveItem() {
        if (locked) {
            LangMsg("actions.sell-locked").sendMessage(player)
            return
        }
        val activeMaterial = HoarderEvent.activeMaterial

        for (item in inventory.contents) {
            if (item != null && item.type == activeMaterial) {
                amountSold += item.amount
                item.amount = 0
            }
        }
        Settings.getDataManger().addPoints(player.uniqueId.toString(), amountSold)

        var sellPrice = 0.0

        if (Settings.usingEconomy() && amountSold > 0) {
            sellPrice = HoarderEvent.activeSellPrice

            payoutAmount = sellPrice * amountSold

            val econHandler: EconomyHandler = when (Settings.getEconomyProvider()) {
                ProviderType.VAULT -> VaultHook()
                ProviderType.PLAYERPOINTS -> PlayerPointsHook()
            }

            var econHandlerResponse = econHandler.addBalance(payoutAmount, player)
            if (econHandlerResponse is Int) econHandlerResponse = econHandlerResponse.toDouble()
            payoutString = Util.formatEconAmt(econHandlerResponse as Double)
        }

        Bukkit.getPluginManager().callEvent(HoarderSellEvent(player, amountSold, sellPrice))


        val msg = if (amountSold > 0) LangMsg("actions.sell").getMsgSendSound(player).format(amountSold.toString(), payoutString) else LangMsg("actions.sell-none").getMsgSendSound(player)
        player.sendMessage(msg)
    }
}