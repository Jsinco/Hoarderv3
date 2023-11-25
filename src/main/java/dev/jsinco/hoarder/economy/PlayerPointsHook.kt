package dev.jsinco.hoarder.economy

import dev.jsinco.hoarder.objects.HoarderPlayer
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

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

    override fun addBalance(amount: Double, player: OfflinePlayer) {
        playerPoints?.give(player.uniqueId, amount.toInt())
    }

    override fun addBalance(amount: Double, hoarderPlayer: HoarderPlayer) {
        playerPoints?.give(UUID.fromString(hoarderPlayer.uuid), amount.toInt())
    }

    override fun takeBalance(amount: Double, player: OfflinePlayer) {
        playerPoints?.take(player.uniqueId, amount.toInt())
    }


}