package dev.lysithea.franziska.core.database.repositories

import com.mongodb.client.model.UpdateOptions
import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * Implementation of [FranziskaSettingRepository].
 *
 * @property database coroutine capable database
 */
class FranziskaSettingRepositoryProvider(
    private val database: CoroutineDatabase
) : FranziskaSettingRepository {

    /**
     * Map of currently loaded settings.
     */
    private val cache = ConcurrentHashMap<String, FranziskaSetting>()

    override suspend fun getOrDefault(guildId: Snowflake?): FranziskaSetting {
        if (guildId == null) return FranziskaSetting(guildId = "")
        val cached = cache[guildId.asString]
        if (cached != null) return cached

        val settings = database
            .getCollection<FranziskaSetting>("settings")
            .findOne(FranziskaSetting::guildId eq guildId.asString)
            ?: FranziskaSetting(guildId = guildId.asString)

        cache[guildId.asString] = settings
        return settings
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