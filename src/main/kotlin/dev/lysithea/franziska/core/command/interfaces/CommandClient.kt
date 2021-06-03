package dev.lysithea.franziska.core.command.interfaces

import dev.kord.core.Kord
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.lysithea.franziska.core.command.abstractions.AbstractCommand
import dev.lysithea.franziska.core.permission.PermissionHandler
import kotlinx.coroutines.Job
import dev.kord.core.on
import kotlin.coroutines.CoroutineContext

/**
 * Client for parsing incoming [AbstractCommand](commands).
 *
 * @param T the [FranziskaContext](context) this client is intended for.
 * @param K the [AbstractCommand] inheritor this client is intended for.
 */
interface CommandClient<T: FranziskaContext, K: AbstractCommand> : CommandRegistry<K> {
    /**
     * The [CoroutineContext] to execute this command on.
     */
    val executor: CoroutineContext

    /**
     * The [PermissionHandler] that should be used to verify if user has sufficient permission
     * to execute a [AbstractCommand](command).
     */
    val permissionHandler: PermissionHandler

    /**
     * Handles errors during command invocations.
     * @see ErrorHandler
     */
    val errorHandler: ErrorHandler<T>

    /**
     * Register a listener for [InteractionCreateEvent]
     * @see on
     */
    fun Kord.onInteraction(): Job = null!!

    /**
     * Register a listener for [MessageCreateEvent]
     * @see on
     */
    fun Kord.onMessageReceived(): Job = null!!
}