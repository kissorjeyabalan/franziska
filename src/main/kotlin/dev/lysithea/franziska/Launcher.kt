package dev.lysithea.franziska

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import dev.lysithea.franziska.di.franziskaModules
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory

suspend fun main(vararg args: String) {
    startKoin {
        modules(franziskaModules)
    }

    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = Level.DEBUG

    LaunchProvider.launch(*args)
}

class LaunchProvider {
    companion object : KoinComponent{
        suspend fun launch(vararg args: String) {
            val franziska by inject<FranziskaBot>()
            franziska.start(true)
        }
    }
}