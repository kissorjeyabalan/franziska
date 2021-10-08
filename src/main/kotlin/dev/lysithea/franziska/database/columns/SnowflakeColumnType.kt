package dev.lysithea.franziska.database.columns

import dev.kord.common.entity.Snowflake
import mu.KotlinLogging
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.vendors.currentDialect

private val logger = KotlinLogging.logger {}

/**
 * Represents a [ColumnType] which wraps a [Snowflake] as a [Long].
 *
 * @see Snowflake
 * @see Snowflake.value
 */
object SnowflakeColumnType : ColumnType() {
    override fun sqlType() = currentDialect.dataTypeProvider.longType()

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is Snowflake -> value
            is Long -> Snowflake(value)
            is String -> Snowflake(value)
            else -> {
                logger.warn { "Unexpected snowflake type: ${value::class.qualifiedName}" }
                Snowflake(value.toString())
            }
        }
    }

    override fun valueToDB(value: Any?): Any {
        require(value is Snowflake) { "Invalid value. Expected: Snowflake, Got: ${value!!::class.qualifiedName}" }
        return value.value.toLong()
    }

    override fun valueToString(value: Any?) = value.toString()
    override fun notNullValueToDB(value: Any): Any = valueToDB(value)
}
