package dev.lysithea.franziska.database.tables

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.config.config
import dev.lysithea.franziska.database.snowflake
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

/**
 * Represents the table of guilds in the database.
 *
 * @property id id row of the table, is primary key
 * @property prefix prefix used to trigger commands
 * @property blacklisted server is blacklisted, ignore all command execution
 */
object GuildTable : IdTable<Snowflake>() {
    override val id: Column<EntityID<Snowflake>> = snowflake("id").entityId()
    val prefix: Column<String> = varchar("prefix", 2).default(config.defaultPrefix)
    val blacklisted: Column<Boolean> = bool("blacklisted").default(false)

    override val primaryKey = PrimaryKey(id)
}
