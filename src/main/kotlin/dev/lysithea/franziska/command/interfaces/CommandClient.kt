package dev.lysithea.franziska.command.interfaces

import dev.kord.core.Kord
import dev.lysithea.franziska.command.abstractions.AbstractCommand
import dev.lysithea.franziska.core.permission.PermissionHandler
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface CommandClient<T: FranziskaContext, K: AbstractCommand> : CommandRegistry<K> {
    val executor: CoroutineContext
    val errorHandler: ErrorHandler<T>
    val permissionHandler: PermissionHandler

    fun Kord.onInteraction(): Job
    fun Kord.onMessageReceived(): Job
}