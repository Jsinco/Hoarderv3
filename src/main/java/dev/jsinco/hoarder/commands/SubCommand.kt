package dev.jsinco.hoarder.commands

import dev.jsinco.hoarder.Hoarder
import org.bukkit.command.CommandSender

/**
 * Interface for subcommands
 */
interface SubCommand {

    /**
     * Executed code for the subcommand
     * @param plugin The plugin instance
     * @param sender The sender of the command
     * @param args The arguments of the command
     */
    fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>)

    /**
     * Tab completion for the subcommand
     * @param plugin The plugin instance
     * @param sender The sender of the command
     * @param args The arguments of the command
     * @return A list of possible tab completions
     */
    fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>?

    /**
     * The permission node required to execute the subcommand
     * @return The permission required to execute the subcommand
     */
    fun permission(): String?

    /**
     * Whether or not the subcommand can only be executed by a player
     * @return Whether or not the subcommand can only be executed by a player
     */
    fun playerOnly(): Boolean
}