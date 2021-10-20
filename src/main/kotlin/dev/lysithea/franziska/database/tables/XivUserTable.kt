package dev.lysithea.franziska.database.tables

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.database.snowflake
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

/**
 * Represents details about FFXIV character for designated user in the database.
 *
 * @property id id row of the table, is primary key, is equals discord user id
 * @property xivId id of character assigned to user
 * @property avatar_url url to user's character avatar
 * @property characterName name user's character
 * @property serverSlug name of server user belongs to
 * @property regionSlug eu/na/jp
 * @property logsCanonicalId fflogs canonical id for character
 * @property verified whether user has verified ownership of character or not
 */
object XivUserTable : IdTable<Snowflake>() {
    override val id: Column<EntityID<Snowflake>> = snowflake("id").entityId()
    val xivId = long("xiv_id")
    val avatarUrl = varchar("avatar_url", 100)
    val characterName = varchar("character_name", 21)
    val serverSlug = varchar("server_slug", 20)
    val regionSlug = varchar("region_slug", 20)
    val logsCanonicalId = long("logs_canonical_id")
    val verified = bool("verified").default(false)
}
