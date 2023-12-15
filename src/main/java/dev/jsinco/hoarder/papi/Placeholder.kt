package dev.jsinco.hoarder.papi

import dev.jsinco.hoarder.Hoarder
import org.bukkit.OfflinePlayer

interface Placeholder {

    fun onReceivedRequest(plugin: Hoarder, player: OfflinePlayer?, args: List<String>): String?
}