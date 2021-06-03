package dev.lysithea.franziska.di

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import dev.lysithea.franziska.core.config.Config
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.database.MongoService
import org.koin.core.Koin
import org.koin.dsl.module
import java.io.File

/**
 * Modules for dependency injection with [Koin].
 */
val franziskaModules = module {
    single {
        ConfigLoader.Builder()
            .addSource(PropertySource.file(File("config.yaml"), true))
            .addSource(PropertySource.file(File("../../config.yaml"), true))
            .build()
            .loadConfigOrThrow<Config>()
    }
    single<DataService> { MongoService() }
}