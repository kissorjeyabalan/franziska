package dev.lysithea.franziska.core.command

import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.lysithea.franziska.FranziskaBot
import dev.lysithea.franziska.core.command.interfaces.FranziskaContext
import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import dev.lysithea.franziska.core.permission.PermissionHandler
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.dsl.createMessage

/**
 * Context of a [AbstractSyntaxCommand] execution.
 *
 * @property permissionHandler the [PermissionHandler] to use during permission checks.
 * @property command the [AbstractSyntaxCommand] that was invoked.
 * @property args the arguments of the command.
 * @property member the [Member] that invoked this [AbstractSyntaxCommand].
 * @property event the [MessageCreateEvent] that triggered this [AbstractSyntaxCommand].
 */
data class SyntaxContext(
    override val franziska: FranziskaBot,
    override val guildSettings: FranziskaSetting,
    val permissionHandler: PermissionHandler,
    val command: AbstractSyntaxCommand,
    val args: List<String>,
    val member: Member,
    val event: MessageCreateEvent
) : FranziskaContext {
    override suspend fun respond(content: String): Message {
        return event.message.channel.createMessage(content)
    }

    override suspend fun respond(embedBuilder: EmbedBuilder): Message {
        return event.message.channel.createMessage(embedBuilder)
    }
    /**
     * The [MessageChannelBehavior] for this [event].
     */
    val channel: MessageChannelBehavior
        get() = event.message.channel

    /**
     * The [command] with prefix from [guildSettings].
     */
    val commandWithPrefix: String
        get() = "${guildSettings.prefix}${command.name}"
}