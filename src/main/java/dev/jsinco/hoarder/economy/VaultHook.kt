package dev.jsinco.hoarder.economy

import dev.jsinco.hoarder.objects.HoarderPlayer
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class VaultHook : EconomyHandler {

    private val economy: Economy? = if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
        Bukkit.getServicesManager().getRegistration(Economy::class.java)!!.provider
    } else {
        null
    }

    override fun getHandlerType(): ProviderType {
        return ProviderType.VAULT
    }

    override fun getBalance(player: OfflinePlayer): Double {
        return economy?.getBalance(player) ?: -1.0
    }

    override fun addBalance(amount: Double, player: OfflinePlayer) {
        economy?.depositPlayer(player, amount)
    }

    override fun addBalance(amount: Double, hoarderPlayer: HoarderPlayer) {
        economy?.depositPlayer(hoarderPlayer.getOfflinePlayer(), amount)
    }

    override fun takeBalance(amount: Double, player: OfflinePlayer) {
        economy?.withdrawPlayer(player, amount)
    }

}