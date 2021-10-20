package dev.lysithea.franziska.database.entities

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.database.tables.XivUserTable
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Representation of a FFXIV character.
 */
interface XivUserEntity {
    val discordId: Snowflake
    val xivId: Long
    val avatarUrl: String
    val characterName: String
    val serverSlug: String
    val regionSlug: String
    val logsCanonicalId: Long
    val verified: Boolean
}

open class XivUser(id: EntityID<Snowflake>) : SnowflakeEntity(id), XivUserEntity {
    override val discordId: Snowflake get() = id.value
    override var xivId: Long by XivUserTable.xivId
    override var avatarUrl: String by XivUserTable.avatarUrl
    override var characterName: String by XivUserTable.characterName
    override var serverSlug: String by XivUserTable.serverSlug
    override var regionSlug: String by XivUserTable.regionSlug
    override var logsCanonicalId: Long by XivUserTable.logsCanonicalId
    override var verified: Boolean by XivUserTable.verified

    companion object : SnowflakeEntityClass<XivUser>(XivUserTable) {
        fun find(id: Snowflake): XivUserEntity? = findById(id)
        fun find(id: Long): XivUserEntity? = findById(Snowflake(id))
    }
}
