package dev.lysithea.franziska.command

import dev.lysithea.franziska.command.interfaces.ErrorHandler
import dev.lysithea.franziska.command.interfaces.FranziskaContext
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [ErrorHandler] that does nothing worthwhile.
 */
class DebugErrorHandler<T : FranziskaContext> : ErrorHandler<T> {
    private val log = KotlinLogging.logger {}
    override fun handleException(
        exception: Throwable,
        context: T,
        thread: Thread,
        coroutineContext: CoroutineContext?
    ) {
        log.error { "An error occurred while executing command in $context" }
        context.franziska.launch {
            context.respond("An error has occurred while executing command. This error has not been handled since Franziska is in development mode.")
        }
    }
}