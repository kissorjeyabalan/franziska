package dev.lysithea.franziska.core.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FranziskaUser(
    @SerialName("discord_id") val discordId: Snowflake,
    @SerialName("character_name") val characterName: String,
    @SerialName("server_name") val serverName: String,
    @SerialName("xiv_id") val xivId: String?,
    @SerialName("verified") val verified: Boolean = false,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)