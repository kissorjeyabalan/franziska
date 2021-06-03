package dev.lysithea.franziska.command.interfaces

import dev.lysithea.franziska.command.abstractions.AbstractCommand

/**
 * Registry of [AbstractCommand]s.
 * @property commands a map between command names and their respective [AbstractCommand]s.
 * @property availableCommands a list of registered commands.
 * @param T the type of command inherited from [AbstractCommand].
 */
interface CommandRegistry<T: AbstractCommand> {
    val commands: MutableMap<String, T>
    val availableCommands: List<T> get() = commands.values.distinct()

    /**
     * Registers multiple [commands] to make them available for use.
     */
    fun registerCommands(vararg commands: T): Unit = commands.forEach { registerCommand(it) }

    private fun registerCommand(command: T) {
        commands[command.name] = command
    }
}