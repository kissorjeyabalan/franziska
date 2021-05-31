package dev.lysithea.franziska.core.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FranziskaSetting(
    @SerialName("guild_id") val guildId: Snowflake,
    @SerialName("enabled") val enabled: Boolean = true,
    @SerialName("prefix") val prefix: String = "%"
)