package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.HoarderEvent
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.manager.SellingManager
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.objects.LangMsg
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EventCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        val dataManager = Settings.getDataManger()

        val string: String = when (args[1]) {
            "restart" -> {
                HoarderEvent.endHoarderEvent()
                HoarderEvent.startHoarderEvent(Settings.getEventTimerLength())
                ""
            }
            "material" -> {
                val material = Material.valueOf(args[2].uppercase())
                dataManager.setEventMaterial(material)
                HoarderEvent.activeMaterial = material
                material.name
            }
            "time" -> {
                val time = args[2].toLong()
                dataManager.setEventEndTime(System.currentTimeMillis() + time)
                HoarderEvent.endTime = System.currentTimeMillis() + (time * 60000)
                time.toString()
            }
            "price" -> {
                if (!Settings.usingEconomy()) return
                val price = args[2].replace(",","").replace("$","").trim().toDouble()
                dataManager.setEventSellPrice(price)
                HoarderEvent.activeSellPrice = price
                args[2]
            }
            "lock" -> {
                if (SellingManager.locked) {
                    SellingManager.locked = false
                    "unlocked"
                } else {
                    SellingManager.locked = true
                    "locked"
                }
            }
            else -> ""
        }
        //sender.sendMessage(Msg("commands.event.${args[1]}").getMsgSendSound(sender as? Player))
        sender.sendMessage(LangMsg("commands.event.${args[1]}").getMsgSendSound(sender as? Player).format(string))
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        if (args.size == 2) {
            val list = mutableListOf("restart", "material", "time", "lock")
            if (Settings.usingEconomy()) list.add("price")
            return list
        }

        return when (args[1]) {
            "material" -> {
                Material.entries.map { it.name.lowercase() }.toMutableList()
            }

            "time" -> {
                mutableListOf("1", "5", "10", "30", "60")
            }

            "price" -> {
                mutableListOf("$10", "$1,000", "$10,000", "$100,000")
            }

            else -> null
        }
    }

    override fun permission(): String {
        return "hoarder.command.event"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}