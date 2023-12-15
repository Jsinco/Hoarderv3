package dev.jsinco.hoarder.papi.placeholders

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.papi.Placeholder
import org.bukkit.OfflinePlayer

class MePlaceholder : Placeholder {
    override fun onReceivedRequest(plugin: Hoarder, player: OfflinePlayer?, args: List<String>): String? {
        if (player != null && args.size >= 2) {
            return when(args[1].lowercase()) {
                "points" -> {
                    Settings.getDataManger().getPoints(player.uniqueId.toString()).toString()
                }

                "claimable" -> {
                    Settings.getDataManger().getClaimableTreasures(player.uniqueId.toString()).toString()
                }

                else -> {
                    null
                }
            }
        }
        return null
    }
}