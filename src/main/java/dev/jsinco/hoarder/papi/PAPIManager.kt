package dev.jsinco.hoarder.papi

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.papi.placeholders.ActivesPlaceholder
import dev.jsinco.hoarder.papi.placeholders.MePlaceholder
import dev.jsinco.hoarder.papi.placeholders.TimePlaceholder
import dev.jsinco.hoarder.papi.placeholders.TopPlaceholder

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class PAPIManager(val plugin: Hoarder) : PlaceholderExpansion() {

    companion object {
        private val placeHolders: MutableMap<String, Placeholder> = mutableMapOf()
    }

    init {
        placeHolders["top"] = TopPlaceholder()
        placeHolders["me"] = MePlaceholder()
        placeHolders["time"] = TimePlaceholder()
        placeHolders["active"] = ActivesPlaceholder()

        plugin.logger.info("Successfully hooked into PlaceholderAPI! Registered: ${placeHolders.size} placeholders")
    }


    override fun getIdentifier(): String {
        return "hoarder"
    }

    override fun getAuthor(): String {
        return "Jsinco"
    }

    override fun getVersion(): String {
        return plugin.description.version
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        val args: List<String> = params.split("_")

        return placeHolders[args[0]]?.onReceivedRequest(plugin, player, args) ?: return null
    }
}