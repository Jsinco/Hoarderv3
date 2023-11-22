package dev.jsinco.hoarder.economy

import dev.jsinco.hoarder.manager.PluginHooks
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class PlayerPointsHook : EconomyHandler {

    val playerPoints: PlayerPointsAPI? = if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
        PlayerPoints.getInstance().api
    } else {
        null
    }

    override fun getHandlerType(): PluginHooks.EconomyProviders {
        return PluginHooks.EconomyProviders.PLAYERPOINTS
    }

    override fun getBalance(player: OfflinePlayer): Any {
        if (playerPoints == null) return -1

        return playerPoints.look(player.uniqueId)
    }

    override fun addBalance(amount: Double, player: OfflinePlayer) {
        playerPoints?.give(player.uniqueId, amount.toInt())
    }

    override fun takeBalance(amount: Any, player: OfflinePlayer) {
        playerPoints.take()
    }


}