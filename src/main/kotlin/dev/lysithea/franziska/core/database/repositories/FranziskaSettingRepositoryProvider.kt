package dev.lysithea.franziska.core.database.repositories

import com.mongodb.client.model.UpdateOptions
import dev.kord.cache.api.find
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
import dev.kord.cache.map.MapDataCache
import dev.kord.cache.map.lruLinkedHashMap
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import org.litote.kmongo.eq
import org.litote.kmongo.set

class FranziskaSettingRepositoryProvider(
    private val database: CoroutineDatabase
) : FranziskaSettingRepository {

    private val cache = MapDataCache {
        forType<FranziskaSetting> { lruLinkedHashMap(10) }
    }

    override suspend fun getOrDefault(guildId: Snowflake?): FranziskaSetting {
        if (guildId == null) return FranziskaSetting(guildId = Snowflake.min)
        val cached = cache.query<FranziskaSetting> { FranziskaSetting::guildId eq guildId }.singleOrNull()
        if (cached != null) return cached

        val fromDatabase = database
            .getCollection<FranziskaSetting>("settings")
            .findOne(FranziskaSetting::guildId eq guildId)
            ?: FranziskaSetting(guildId = guildId)

        cache.put(fromDatabase)
        return fromDatabase
    }

    override suspend fun upsert(setting: FranziskaSetting): Boolean {
        val upserted = database
            .getCollection<FranziskaSetting>("settings")
            .updateOne(FranziskaSetting::guildId eq setting.guildId, setting, UpdateOptions().upsert(true))
        cache.remove<FranziskaSetting> { FranziskaSetting::guildId eq setting.guildId }
        cache.put(setting)
        return upserted.modifiedCount == 1L || upserted.upsertedId != null
    }
}