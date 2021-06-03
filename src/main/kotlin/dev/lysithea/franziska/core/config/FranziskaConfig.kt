package dev.lysithea.franziska.core.config

import dev.kord.common.entity.Snowflake

/**
 * Representation of bot configuration.
 * @property token the production token for Discord bot.
 * @property devToken the development token for Discord bot.
 * @property status a message to display as presence status on Discord.
 * @property owner the [Snowflake] of the owner of this bot.
 */
data class FranziskaConfig(val token: String, val devToken: String, val status: String, val owner: String)