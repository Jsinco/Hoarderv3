package dev.jsinco.hoarder.objects

import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.utilities.Util
import org.bukkit.Sound
import org.bukkit.entity.Player

class Msg(path: String) {
    private val file = FileManager("messages.yml").generateYamlFile()

    val prefix = file.getString("prefix") ?: ""
    val message = if (file.getString("$path.message") == null) file.getString(path) else file.getString("$path.message")

    val sound = if (file.getString("$path.sound") != null) file.getString("$path.sound") else null
    val volume: Float = if (file.get("$path.volume") != null) file.getDouble("$path.volume").toFloat() else 0.0f
    val pitch: Float = if (file.get("$path.pitch") != null) file.getDouble("$path.pitch").toFloat() else 0.0f

    fun sendMessage(player: Player) {
        player.sendMessage(Util.fullColor(prefix + message))
        if (sound != null) {
            player.playSound(player.location, Sound.valueOf(sound), volume, pitch)
        }
    }

    fun getMsgSendSound(player: Player): String {
        if (sound != null) {
            player.playSound(player.location, Sound.valueOf(sound), volume, pitch)
        }
        return Util.fullColor(prefix + message)
    }
}