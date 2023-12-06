package dev.jsinco.hoarder.commands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.subcommands.*
import dev.jsinco.hoarder.gui.enums.Action
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CommandManager(val plugin: Hoarder) : CommandExecutor, TabCompleter {

    private val subCommands: MutableMap<String, SubCommand> = mutableMapOf()

    init {
        subCommands["gui"] = OpenGUICommand()
        subCommands["reload"] = ReloadCommand()
        subCommands["treasure"] = TreasureCommand()
        subCommands["claim"] = ClaimTreasureCommand()
        subCommands["debug"] = DebugCommand()
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            val actionString = Settings.defaultCommandAction()
            if (actionString != "NONE" && sender is Player) {
                val parsedAction = Action.parseStringAction(actionString)
                parsedAction.first.executeAction(parsedAction.second, sender, null)
            }
            return true
        }
        val subCommand = subCommands[args[0]] ?: return true

        if (subCommand.permission() != null && !sender.hasPermission(subCommand.permission()!!)) return true
        else if (subCommand.playerOnly() && sender !is Player) return true

        subCommand.execute(plugin, sender, args)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        val subCommandKeys: MutableList<String> = mutableListOf()
        if (args.size == 1) {
            for (key in subCommands.keys) {
                if (subCommands[key]?.permission() == null) {
                    subCommandKeys.add(key)
                } else if (sender.hasPermission(subCommands[key]!!.permission()!!)) {
                    subCommandKeys.add(key)
                }
            }
            return subCommandKeys
        }

        val subCommand = subCommands[args[0]] ?: return null
        return subCommand.tabComplete(plugin, sender, args)
    }
}