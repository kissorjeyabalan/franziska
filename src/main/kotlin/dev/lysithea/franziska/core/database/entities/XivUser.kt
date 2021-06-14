@file:Suppress("unused")

package dev.lysithea.franziska.core.database.entities

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a Final Fantasy XIV user.
 *
 * @property discordId the [Member] this xiv user belongs to.
 * @property characterName the name of xiv character.
 * @property serverName the name of the server this character belongs to.
 * @property xivId the lodestone id of this character.
 * @property verified if the user has verified owning this character via lodestone.
 * @property createdAt the epoch timestamp this [xivId] was associated with [discordId]
 */
@Serializable
data class XivUser(
    @SerialName("discord_id") val discordId: String,
    @SerialName("character_name") val characterName: String,
    @SerialName("server_name") val serverName: String,
    @SerialName("xiv_id") val xivId: String,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)