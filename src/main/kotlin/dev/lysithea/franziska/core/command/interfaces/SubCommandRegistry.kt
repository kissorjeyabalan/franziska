package dev.lysithea.franziska.core.command.interfaces

import dev.lysithea.franziska.core.command.abstractions.AbstractCommand

/**
 * Registry of [AbstractCommand]s as subcommands.
 * @property subCommands a map between subcommand names and their respective [AbstractCommand]s.
 * @param T the type of subcommand inherited from [AbstractCommand].
 */
interface SubCommandRegistry<T: AbstractCommand> {
    val subCommands: MutableMap<String, T>
    /**
     * Registers multiple [commands] to make them available for use.
     */
    fun registerSubCommands(vararg commands: T): Unit = commands.forEach { registerSubCommand(it) }

    private fun registerSubCommand(command: T) {
        subCommands[command.name] = command
    }
}