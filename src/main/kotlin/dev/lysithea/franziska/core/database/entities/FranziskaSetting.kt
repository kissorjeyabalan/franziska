package dev.lysithea.franziska.core.database.entities

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.core.permission.PermissionLevel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representation of guild configurations.
 *
 * @property guildId the guild this setting applies to.
 * @property enabled if bot is enabled for this guild.
 * @property prefix the prefix to use for parsing [AbstractSyntaxCommand]s.
 * @property permissions map of [PermissionLevel] for users in this guild.
 */
@Serializable
data class FranziskaSetting(
    @SerialName("guild_id") val guildId: Snowflake,
    @SerialName("enabled") val enabled: Boolean = true,
    @SerialName("prefix") val prefix: String = "%",
    @SerialName("permissions") val permissions: Map<Snowflake, PermissionLevel> = mapOf()
)