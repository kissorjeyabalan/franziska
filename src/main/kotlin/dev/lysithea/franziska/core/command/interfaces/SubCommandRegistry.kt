package dev.lysithea.franziska.core.command.interfaces

import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.abstractions.AbstractCommand
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand

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

    suspend fun executeSubCommand(context: SyntaxContext) {
        val subCommand = subCommands[context.args[0].lowercase()] as? AbstractSyntaxCommand
        if (subCommand != null) {
            subCommand.execute(context.copy(args = context.args.drop(1), command = subCommand))
        } else {
            context.respond(Embeds.error("Command not found", "${context.args[0]} is not a valid subcommand."))
        }
    }
}