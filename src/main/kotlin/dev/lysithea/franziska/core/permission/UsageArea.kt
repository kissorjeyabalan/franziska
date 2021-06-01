package dev.lysithea.franziska.core.permission

import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.entity.interaction.DmInteraction
import dev.kord.core.entity.interaction.GuildInteraction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent

enum class UsageArea {
    DIRECT_MESSAGE,
    GUILD_MESSAGE,
    ANY;

    fun matches(event: InteractionCreateEvent): Boolean {
        return when (this) {
            ANY -> true
            DIRECT_MESSAGE -> event.interaction is DmInteraction
            GUILD_MESSAGE -> event.interaction is GuildInteraction
        }
    }

    fun matches(event: MessageCreateEvent): Boolean {
        return when (this) {
            ANY -> true
            DIRECT_MESSAGE -> event.message.channel is DmChannel
            GUILD_MESSAGE -> event.message.channel is GuildMessageChannelBehavior
        }
    }
}