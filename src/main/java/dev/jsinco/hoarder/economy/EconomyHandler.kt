package dev.jsinco.hoarder.economy

import dev.jsinco.hoarder.objects.HoarderPlayer
import org.bukkit.OfflinePlayer

interface EconomyHandler {

    fun getHandlerType(): ProviderType

    fun getBalance(player: OfflinePlayer): Double

    fun addBalance(amount: Double, player: OfflinePlayer)

    fun addBalance(amount: Double, hoarderPlayer: HoarderPlayer)

    fun takeBalance(amount: Double, player: OfflinePlayer)
}