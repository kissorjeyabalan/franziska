package dev.lysithea.franziska.database

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.database.columns.SnowflakeColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/**
 * Registers a column of type [Snowflake] to table.
 *
 * @see SnowflakeColumnType
 */
fun Table.snowflake(name: String): Column<Snowflake> = registerColumn(name, SnowflakeColumnType)
