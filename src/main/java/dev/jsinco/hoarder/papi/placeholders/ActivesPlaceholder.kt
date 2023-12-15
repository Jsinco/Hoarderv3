package dev.jsinco.hoarder.papi.placeholders

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.papi.Placeholder
import dev.jsinco.hoarder.utilities.Util
import org.bukkit.OfflinePlayer

class ActivesPlaceholder : Placeholder {
    override fun onReceivedRequest(plugin: Hoarder, player: OfflinePlayer?, args: List<String>): String? {
        if (args.size < 3) return null

        return when(args[1].lowercase()) {
            "material" -> {
                return Settings.getDataManger().getEventMaterial().name
            }

            "materialformatted" -> {
                return Util.formatMaterialName(Settings.getDataManger().getEventMaterial())
            }

            "sellprice" -> {
                return Settings.getDataManger().getEventSellPrice().toString()
            }

            else -> {
                null
            }
        }
    }
}