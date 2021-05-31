package dev.lysithea.franziska.core.database.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import org.litote.kmongo.eq

class FranziskaSettingRepositoryProvider(
    private val database: CoroutineDatabase
) : FranziskaSettingRepository {

    override suspend fun getSettingOrDefault(guildId: Snowflake): FranziskaSetting {
        return database
            .getCollection<FranziskaSetting>("settings")
            .findOne(FranziskaSetting::guildId eq guildId)
            ?: FranziskaSetting(guildId = guildId)
    }
}