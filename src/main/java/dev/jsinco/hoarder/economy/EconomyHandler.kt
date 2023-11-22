package dev.jsinco.hoarder.economy

import dev.jsinco.hoarder.manager.PluginHooks
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

interface EconomyHandler {

    fun getHandlerType(): PluginHooks.EconomyProviders

    fun getBalance(player: OfflinePlayer): Any

    fun addBalance(amount: Double, player: OfflinePlayer)

    fun takeBalance(amount: Any, player: OfflinePlayer)
}