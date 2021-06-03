package dev.lysithea.franziska.core.command.abstractions

import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.interfaces.CommandRegistry

/**
 * Abstract implementation of a command that is accepted via text messages.
 *
 */
abstract class AbstractSyntaxCommand : AbstractCommand(), CommandRegistry<AbstractSyntaxCommand> {
    override val commands = mutableMapOf<String, AbstractSyntaxCommand>()
    abstract suspend fun execute(context: SyntaxContext)
}