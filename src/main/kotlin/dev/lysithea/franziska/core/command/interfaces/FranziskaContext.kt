package dev.lysithea.franziska.core.command.interfaces

import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.entity.Message
import dev.lysithea.franziska.FranziskaBot
import dev.lysithea.franziska.core.database.entities.FranziskaSetting

/**
 * Interface defining the required properties and methods
 * for the context of a command invocation.
 *
 * @property franziska the instance of [FranziskaBot].
 * @property guildSettings the [FranziskaSetting] for the executing guild.
 */
interface FranziskaContext {
    val franziska: FranziskaBot
    val guildSettings: FranziskaSetting

    /**
     * Sends [content] to the corresponding [ChannelBehavior].
     * @return the [Message] that was created.
     */
    suspend fun respond(content: String): Message
}