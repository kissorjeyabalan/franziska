package dev.lysithea.franziska.config.spec

import com.uchuhimo.konf.ConfigSpec

object BotSpec : ConfigSpec() {
    val token by required<String>(description = "discord bot token")
    val emojiGuild by optional(0L, description = "emoji guild id")
    val commandPrefix by required<String>(name = "prefix", description = "default command prefix")
    val testServer by optional(818888629585707020, name = "testGuild", description = "test guild")
}
