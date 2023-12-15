package dev.jsinco.hoarder.papi.placeholders

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.objects.Time
import dev.jsinco.hoarder.papi.Placeholder
import org.bukkit.OfflinePlayer

class TimePlaceholder: Placeholder {
    override fun onReceivedRequest(plugin: Hoarder, player: OfflinePlayer?, args: List<String>): String? {
        val time = Time()
        return "${time.hours}h, ${time.mins}m, ${time.secs}s"
    }
}