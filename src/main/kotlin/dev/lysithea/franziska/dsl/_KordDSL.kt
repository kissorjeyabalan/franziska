package dev.lysithea.franziska.dsl

import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.EmbedBuilder

typealias EmbedCreator = EmbedBuilder.() -> Unit

suspend fun MessageChannelBehavior.createMessage(embedBuilder: EmbedBuilder): Message =
    createMessage { embed = embedBuilder }