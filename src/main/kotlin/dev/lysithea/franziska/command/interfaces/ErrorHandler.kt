package dev.lysithea.franziska.command.interfaces

import kotlin.coroutines.CoroutineContext

interface ErrorHandler<T: FranziskaContext> {
    fun handleException(
        exception: Throwable,
        context: T,
        thread: Thread,
        coroutineContext: CoroutineContext? = null
    )
}