package dev.lysithea.franziska.constants

import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.x.emoji.Emojis
import dev.lysithea.franziska.dsl.EmbedCreator

/**
 * Preset [EmbedBuilder]s for frequently used embeds.
 *
 */
object Embeds {
    /**
     * Creates an error embed.
     *
     * @param title the title for this embed
     * @param description the content for this embed
     * @param builder the builder to apply
     * @return
     */
    inline fun error(title: String, description: String?, builder: EmbedCreator = {}): EmbedBuilder =
        EmbedBuilder().apply {
            title(Emojis.warning.unicode, title)
            this.description = description
            color = Colors.LIGHT_RED
        }.apply(builder)

    inline fun success(title: String, description: String?, builder: EmbedCreator = {}): EmbedBuilder =
        EmbedBuilder().apply {
            title(Emojis.whiteCheckMark.unicode, title)
            this.description = description
            color = Colors.LIGHT_GREEN
        }.apply(builder)

    inline fun info(title: String, description: String? = null, builder: EmbedCreator = {}): EmbedBuilder =
        EmbedBuilder().apply {
            title(Emojis.informationSource.unicode, title)
            this.description = description
            color = Colors.BLUE
        }.apply(builder)

    @PublishedApi
    internal fun EmbedBuilder.title(emoji: String, title: String) {
        this.title = "$emoji $title"
    }
}