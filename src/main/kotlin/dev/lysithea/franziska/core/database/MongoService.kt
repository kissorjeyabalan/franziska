package dev.lysithea.franziska.core.database

import dev.lysithea.franziska.core.config.Config
import dev.lysithea.franziska.core.database.repositories.FranziskaSettingRepository
import dev.lysithea.franziska.core.database.repositories.FranziskaSettingRepositoryProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

/**
 * [DataService] implementation using [CoroutineDatabase].
 */
class MongoService() : DataService, KoinComponent {
    private val config by inject<Config>()
    private val database: CoroutineDatabase =
        KMongo.createClient(config.mongo.connectionString).coroutine.getDatabase(config.mongo.database)

    override val settings: FranziskaSettingRepository = FranziskaSettingRepositoryProvider(database)
}