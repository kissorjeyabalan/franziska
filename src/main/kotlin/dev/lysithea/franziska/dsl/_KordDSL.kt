package dev.lysithea.franziska.dsl

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.EmbedBuilder

typealias EmbedCreator = EmbedBuilder.() -> Unit

suspend fun MessageChannelBehavior.createMessage(embedBuilder: EmbedBuilder): Message =
    createMessage { embed = embedBuilder }

internal val mentionRegex = Regex("(?:<@!?)?([0-9]+)>?")

fun String.getUserMention(): String =
    mentionRegex.matchEntire(trim())?.groupValues?.get(1) ?: ""

suspend fun String.getMentionedUser(guild: Guild): Member? {
    return getUserMention().let {
        val snowFlake = try {
            Snowflake(it)
        } catch (thr: Throwable) {
            null
        } ?: return@let null

        return@let guild.getMemberOrNull(snowFlake)
    }
}