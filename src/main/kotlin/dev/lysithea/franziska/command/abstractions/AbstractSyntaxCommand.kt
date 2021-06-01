package dev.lysithea.franziska.command.abstractions

import dev.lysithea.franziska.command.SyntaxContext

abstract class AbstractSyntaxCommand : AbstractCommand() {
    abstract suspend fun execute(context: SyntaxContext)
}