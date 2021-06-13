package dev.lysithea.franziska.core.database.repositories

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.mongodb.client.model.UpdateOptions
import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import kotlinx.coroutines.Deferred
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

/**
 * Implementation of [FranziskaSettingRepository].
 *
 * @property database coroutine capable database
 */
class FranziskaSettingRepositoryProvider(
    private val database: CoroutineDatabase
) : FranziskaSettingRepository {

    /**
     * Cache of currently loaded settings.
     */
    private val cache: Cache<String, FranziskaSetting> = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .build()

    override suspend fun getOrDefault(guildId: Snowflake?): FranziskaSetting {
        if (guildId == null) return FranziskaSetting(guildId = "")
        val cached = cache.getIfPresent(guildId.asString)
        if (cached != null) return cached

        val settings = database
            .getCollection<FranziskaSetting>("settings")
            .findOne(FranziskaSetting::guildId eq guildId.asString)
            ?: FranziskaSetting(guildId = guildId.asString)

        cache.put(guildId.asString, settings)
        return settings
    }

    override suspend fun upsert(setting: FranziskaSetting): Boolean {
        val upserted = database
            .getCollection<FranziskaSetting>("settings")
            .updateOne(FranziskaSetting::guildId eq setting.guildId, setting, UpdateOptions().upsert(true))
        cache.put(setting.guildId, setting)
        return upserted.modifiedCount == 1L || upserted.upsertedId != null
    }
}