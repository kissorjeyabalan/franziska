package dev.lysithea.franziska.core.database.repositories

import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.entities.FranziskaSetting

interface FranziskaSettingRepository {
    suspend fun getOrDefault(guildId: Snowflake): FranziskaSetting
    suspend fun upsert(setting: FranziskaSetting): Boolean
}