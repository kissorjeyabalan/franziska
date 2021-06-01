package dev.lysithea.franziska.command

import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.lysithea.franziska.FranziskaBot
import dev.lysithea.franziska.command.abstractions.AbstractCommand
import dev.lysithea.franziska.command.interfaces.FranziskaContext

data class SyntaxContext(
    override val franziska: FranziskaBot,
    val prefix: String,
    val command: AbstractCommand,
    val args: List<String>,
    val member: Member,
    val event: MessageCreateEvent
) : FranziskaContext {
    override suspend fun respond(content: String): Message {
        return event.message.channel.createMessage(content)
    }
}