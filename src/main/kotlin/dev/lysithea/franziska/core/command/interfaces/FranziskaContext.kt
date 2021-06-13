package dev.lysithea.franziska.core.command.interfaces

import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.EmbedBuilder
import dev.lysithea.franziska.FranziskaBot
import dev.lysithea.franziska.core.FranziskaFeature
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
    suspend fun respond(embedBuilder: EmbedBuilder): Message

    fun isBotOwner(memberId: String): Boolean = franziska.config.franziska.owner == memberId
    fun hasFeature(feature: FranziskaFeature) = guildSettings.enabledFeatures.contains(feature)
}