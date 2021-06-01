package dev.lysithea.franziska.constants

import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.x.emoji.Emojis
import dev.lysithea.franziska.dsl.EmbedCreator

object Embeds {
    inline fun error(title: String, description: String?, builder: EmbedCreator = {}): EmbedBuilder =
        EmbedBuilder().apply {
            title(Emojis.warning.unicode, title)
            this.description = description
            color = Colors.LIGHT_RED
        }.apply(builder)

    @PublishedApi
    internal fun EmbedBuilder.title(emoji: String, title: String) {
        this.title = "$emoji $title"
    }
}