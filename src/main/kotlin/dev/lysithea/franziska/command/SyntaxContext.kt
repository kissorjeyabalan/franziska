package dev.lysithea.franziska.command

import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.lysithea.franziska.FranziskaBot
import dev.lysithea.franziska.command.abstractions.AbstractCommand
import dev.lysithea.franziska.command.interfaces.FranziskaContext
import dev.lysithea.franziska.core.permission.PermissionHandler

data class SyntaxContext(
    override val franziska: FranziskaBot,
    val prefix: String,
    val permissionHandler: PermissionHandler,
    val command: AbstractCommand,
    val args: List<String>,
    val member: Member,
    val event: MessageCreateEvent
) : FranziskaContext {
    override suspend fun respond(content: String): Message {
        return event.message.channel.createMessage(content)
    }

    val channel: MessageChannelBehavior
        get() = event.message.channel

    val commandWithPrefix: String
        get() = "${prefix}${command.name}"
}