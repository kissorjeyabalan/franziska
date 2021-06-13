package dev.lysithea.franziska.core.database.repositories

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.mongodb.client.model.UpdateOptions
import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.core.database.entities.XivUser
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.util.concurrent.TimeUnit

/**
 * Implementation of [XivRepository].
 *
 * @constructor Create empty Xiv repository provider
 */
class XivRepositoryProvider(private val database: CoroutineDatabase) : XivRepository {
    private val userCache: Cache<String, XivUser> = CacheBuilder.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .build()

    override suspend fun getOrNull(userId: Snowflake?): XivUser? {
        if (userId == null) return null
        val cached = userCache.getIfPresent(userId.asString)
        if (cached != null) return cached

        val user = database
            .getCollection<XivUser>("users")
            .findOne(XivUser::discordId eq userId.asString)

        if (user != null)
            userCache.put(userId.asString, user)
        return user
    }

    override suspend fun upsert(xivUser: XivUser): Boolean {
        val upserted = database
            .getCollection<XivUser>("users")
            .updateOne(XivUser::discordId eq xivUser.discordId, xivUser, UpdateOptions().upsert(true))
        userCache.put(xivUser.discordId, xivUser)
        return upserted.modifiedCount == 1L || upserted.upsertedId != null
    }
}