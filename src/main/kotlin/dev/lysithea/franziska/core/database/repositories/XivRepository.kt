package dev.lysithea.franziska.core.database.repositories

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.database.entities.XivUser

interface XivRepository {
    /**
     * Retrieve xiv user from cache or [DataService].
     *
     * @param userId Discord snowflake to retrieve user for.
     * @return [XivUser] or null if no xiv user exists.
     */
    suspend fun getOrNull(userId: Snowflake?): XivUser?

    /***
     * Updates or creates a [XivUser].
     *
     * @param xivUser User to create or update
     * @return If data was persisted
     */
    suspend fun upsert(xivUser: XivUser): Boolean
}