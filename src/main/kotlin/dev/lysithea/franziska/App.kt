package dev.lysithea.franziska

import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.lysithea.franziska.config.buildInfo
import dev.lysithea.franziska.config.config
import dev.lysithea.franziska.di._httpModules
import dev.lysithea.franziska.di._xivModules
import dev.lysithea.franziska.extensions.ffxiv.FashionReportExtension
import mu.KotlinLogging

lateinit var bot: ExtensibleBot

suspend fun main() {
    val logger = KotlinLogging.logger {  }
    bot = ExtensibleBot(config.discordToken) {
        messageCommands { defaultPrefix = "?" }
        extensions {
            sentry {
                if (config.sentryDsn.isNotBlank()) {
                    enable = true
                    dsn = config.sentryDsn
                    environment = config.environment
                }
            }

            add(::FashionReportExtension)
        }
    }

    bot.getKoin().loadModules(listOf(
        _httpModules,
        _xivModules
    ))

    logger.info { "Starting Franziska v${buildInfo.version}" }

    bot.start()
}
