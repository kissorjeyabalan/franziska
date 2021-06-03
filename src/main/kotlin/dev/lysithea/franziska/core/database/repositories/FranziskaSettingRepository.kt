package dev.lysithea.franziska.core.database.repositories

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.database.entities.FranziskaSetting

/**
 * Interface defining required members for guild settings repository.
 *
 */
interface FranziskaSettingRepository {
    /**
     * Gets guild settings from cache or [DataService].
     *
     * @param guildId to retrieve settings for
     * @return [FranziskaSetting] or default.
     */
    suspend fun getOrDefault(guildId: Snowflake?): FranziskaSetting

    /**
     * Updated or creates a [FranziskaSetting].
     *
     * @param setting to create or update
     * @return true if persisted to data service
     */
    suspend fun upsert(setting: FranziskaSetting): Boolean
}