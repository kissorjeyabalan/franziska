package dev.lysithea.franziska.di

import com.sksamuel.hoplite.ConfigLoader
import dev.lysithea.franziska.core.config.Config
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.database.MongoService
import org.koin.core.Koin
import org.koin.dsl.module

/**
 * Modules for dependency injection with [Koin].
 */
val franziskaModules = module {
    single { ConfigLoader().loadConfigOrThrow<Config>("/config.yaml") }
    single<DataService> { MongoService() }
}