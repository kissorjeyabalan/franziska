package dev.lysithea.franziska.command.interfaces

import kotlin.coroutines.CoroutineContext

/**
 * Interface for handling error during command invocation.
 *
 * @param T the context this error handler applies to.
 */
interface ErrorHandler<T: FranziskaContext> {
    /**
     * Handles the [exception] thrown during [context].
     */
    fun handleException(
        exception: Throwable,
        context: T,
        thread: Thread,
        coroutineContext: CoroutineContext? = null
    )
}