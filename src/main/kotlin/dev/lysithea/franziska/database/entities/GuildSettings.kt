@file:Suppress("MemberVisibilityCanBePrivate")

package dev.lysithea.franziska.database.entities

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.database.tables.GuildTable
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Representation of settings for a guild.
 *
 * @property guildId the id of a guild
 * * @property prefix prefix used to execute commands
 * @property blacklisted if server is blacklisted for command execution
 */
interface GuildSettingsEntity {
    val guildId: Snowflake
    var prefix: String
    var blacklisted: Boolean
}

open class GuildSettings(id: EntityID<Snowflake>) : SnowflakeEntity(id), GuildSettingsEntity {
    override val guildId: Snowflake get() = id.value
    override var prefix: String by GuildTable.prefix
    override var blacklisted: Boolean by GuildTable.blacklisted

    companion object : SnowflakeEntityClass<GuildSettings>(GuildTable) {
        fun findOrCreateById(id: Snowflake): GuildSettingsEntity = findById(id) ?: new(id) {}
        fun findOrCreateById(id: Long): GuildSettingsEntity = findOrCreateById(Snowflake(id))
    }
}
