package dev.jsinco.hoarder.papi

import org.bukkit.OfflinePlayer

interface PlaceHolder {

    fun onReceivedRequest(player: OfflinePlayer?, args: List<String>): String
}