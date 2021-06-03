package dev.lysithea.franziska.core.database.repositories

import com.mongodb.client.model.UpdateOptions
import dev.kord.cache.api.*
import dev.kord.cache.api.data.description
import dev.kord.cache.map.MapDataCache
import dev.kord.cache.map.lruLinkedHashMap
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.litote.kmongo.eq
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class FranziskaSettingRepositoryProvider(
    private val database: CoroutineDatabase
) : FranziskaSettingRepository, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob();

    private val cache = ConcurrentHashMap<Snowflake, FranziskaSetting>()

    override suspend fun getOrDefault(guildId: Snowflake?): FranziskaSetting {
        if (guildId == null) return FranziskaSetting(guildId = Snowflake.min)
        val cached = cache[guildId]
        if (cached != null) return cached

        val fromDatabase = database
            .getCollection<FranziskaSetting>("settings")
            .findOne(FranziskaSetting::guildId eq guildId) ?: FranziskaSetting(guildId = guildId)

        cache[guildId] = fromDatabase
        return fromDatabase
    }

    override suspend fun upsert(setting: FranziskaSetting): Boolean {
        val upserted = database
            .getCollection<FranziskaSetting>("settings")
            .updateOne(FranziskaSetting::guildId eq setting.guildId, setting, UpdateOptions().upsert(true))
        cache.remove(setting.guildId)
        cache[setting.guildId] = setting
        return upserted.modifiedCount == 1L || upserted.upsertedId != null
    }
}