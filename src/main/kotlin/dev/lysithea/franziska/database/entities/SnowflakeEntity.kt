package dev.lysithea.franziska.database.entities

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

/**
 * Shorthand for [Entity<Snowflake>][Entity].
 */
abstract class SnowflakeEntity(id: EntityID<Snowflake>) : Entity<Snowflake>(id)

/**
 * Shorthand for [EntityClass<Snowflake>][EntityClass].
 */
abstract class SnowflakeEntityClass<out T : SnowflakeEntity>(table: IdTable<Snowflake>, entityType: Class<T>? = null) :
    EntityClass<Snowflake, T>(table, entityType)
