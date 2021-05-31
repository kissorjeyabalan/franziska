package dev.lysithea.franziska.core.database.repositories

import com.mongodb.client.model.UpdateOptions
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

    override suspend fun getOrDefault(guildId: Snowflake): FranziskaSetting {
        return database
            .getCollection<FranziskaSetting>("settings")
            .findOne(FranziskaSetting::guildId eq guildId)
            ?: FranziskaSetting(guildId = guildId)
    }

    override suspend fun upsert(setting: FranziskaSetting): Boolean {
        val upserted = database
            .getCollection<FranziskaSetting>("settings")
            .updateOne(FranziskaSetting::guildId eq setting.guildId, setting, UpdateOptions().upsert(true))
        return upserted.modifiedCount == 1L || upserted.upsertedId != null
    }
}