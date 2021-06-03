package dev.lysithea.franziska.core.permission

import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.entity.interaction.DmInteraction
import dev.kord.core.entity.interaction.GuildInteraction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent

/**
 * Enumeration defining where a command can be executed.
 */
enum class UsageArea {
    /**
     * Only in direct messages.
     */
    DIRECT_MESSAGE,

    /**
     * Only in guild channels.
     */
    GUILD_MESSAGE,

    /**
     * Anywhere.
     */
    ANY;

    /**
     * Checks where a [InteractionCreateEvent] was executed.
     */
    fun matches(event: InteractionCreateEvent): Boolean {
        return when (this) {
            ANY -> true
            DIRECT_MESSAGE -> event.interaction is DmInteraction
            GUILD_MESSAGE -> event.interaction is GuildInteraction
        }
    }

    /**
     * Checks where a [MessageCreateEvent] was executed.
     */
    fun matches(event: MessageCreateEvent): Boolean {
        return when (this) {
            GUILD_MESSAGE -> event.message.channel !is DmChannel
            DIRECT_MESSAGE -> event.message.channel is DmChannel
            ANY -> true
        }
    }
}