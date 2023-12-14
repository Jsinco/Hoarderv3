package dev.jsinco.hoarder.papi

import dev.jsinco.hoarder.Hoarder
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class PAPIManager(val plugin: Hoarder) : PlaceholderExpansion() {

    private val placeHolders: MutableMap<String, PlaceHolder> = mutableMapOf()

    init {
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

        return placeHolders[args[0]]?.onReceivedRequest(player, args) ?: return null
    }
}