package dev.lysithea.franziska.command.interfaces

import dev.lysithea.franziska.command.abstractions.AbstractCommand

interface CommandRegistry<T: AbstractCommand> {
    val commands: MutableMap<String, T>
    val availableCommands: List<T> get() = commands.values.distinct()

    public fun registerCommands(vararg commands: T): Unit = commands.forEach { registerCommand(it) }
    public fun registerCommand(command: T) {
        commands[command.name] = command
    }
}