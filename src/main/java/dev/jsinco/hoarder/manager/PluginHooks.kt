package dev.jsinco.hoarder.manager

import net.milkbowl.vault.economy.Economy
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit

object PluginHooks {

    val economy: Economy? = if (isEnabled("Vault")) {
        Bukkit.getServicesManager().getRegistration(Economy::class.java)!!.provider
    } else {
        null
    }

    val playerPoints: PlayerPointsAPI? = if (isEnabled("PlayerPoints")) {
        PlayerPoints.getInstance().api
    } else {
        null
    }


    private fun isEnabled(name: String): Boolean = Bukkit.getPluginManager().getPlugin(name) != null


    enum class EconomyProviders {
        VAULT,
        PLAYERPOINTS
    }
}