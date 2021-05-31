package dev.lysithea.franziska.core.di

import com.sksamuel.hoplite.ConfigLoader
import dev.lysithea.franziska.core.config.Config
import dev.lysithea.franziska.core.config.FranziskaConfig
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.database.MongoService
import org.koin.dsl.module

val franziskaModules = module {
    single { ConfigLoader().loadConfigOrThrow<Config>("/config.yaml") }
    single<DataService> { MongoService() }
}