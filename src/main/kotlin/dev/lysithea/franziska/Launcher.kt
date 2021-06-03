package dev.lysithea.franziska

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import dev.lysithea.franziska.di.franziskaModules
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory

/**
 * Launcher!
 */
suspend fun main(vararg args: String) {
    startKoin {
        modules(franziskaModules)
    }

    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = Level.DEBUG

    FranziskaBotProvider().start(false)
}