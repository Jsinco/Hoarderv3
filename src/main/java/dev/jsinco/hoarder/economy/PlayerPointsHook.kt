package dev.jsinco.hoarder.economy

import dev.jsinco.hoarder.objects.HoarderPlayer
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

/**
 * PlayerPointsHook, playerpoints string format "%,d"
 */
class PlayerPointsHook : EconomyHandler {

    private val playerPoints: PlayerPointsAPI? = if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
        PlayerPoints.getInstance().api
    } else {
        null
    }

    override fun getHandlerType(): ProviderType {
        return ProviderType.PLAYERPOINTS
    }

    override fun getBalance(player: OfflinePlayer): Double {
        return playerPoints?.look(player.uniqueId)?.toDouble() ?: -1.0
    }

    override fun addBalance(amount: Double, player: OfflinePlayer): Any {
        val amt = amount.toInt()
        playerPoints?.give(player.uniqueId, amt)
        return amt
    }

    override fun addBalance(amount: Double, hoarderPlayer: HoarderPlayer): Any {
        val amt = amount.toInt()
        playerPoints?.give(UUID.fromString(hoarderPlayer.uuid), amt)
        return amt
    }

    override fun takeBalance(amount: Double, player: OfflinePlayer) {
        playerPoints?.take(player.uniqueId, amount.toInt())
    }


}