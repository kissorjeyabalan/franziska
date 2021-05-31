package dev.lysithea.franziska

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.gateway.ResumedEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.lysithea.franziska.core.config.Config
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.set
import kotlin.coroutines.CoroutineContext

@OptIn(PrivilegedIntent::class)
class FranziskaBotProvider : FranziskaBot, KoinComponent {
    private val log = KotlinLogging.logger { }
    override val coroutineContext = Dispatchers.IO + SupervisorJob()

    private lateinit var kord: Kord
    private val config by inject<Config>()
    private var isInitialized = false

    private fun Kord.listeners() {
        onReady()
        onDisconnected()
        onResumed()
    }

    private fun Kord.onReady() = on<ReadyEvent> {
        log.info { "Ready event received. Initializing Franziska." }
        isInitialized = true
    }

    private fun Kord.onDisconnected() = on<DisconnectEvent> {
        log.info { "Franziska disconnected from Discord." }
        isInitialized = false
    }

    private fun Kord.onResumed() = on<ResumedEvent> {
        log.info { "Franziska reconnected to Discord. " }
        isInitialized = true
    }

    suspend fun start() {
        kord = Kord(config.franziska.token) {
            httpClient = HttpClient(CIO)
            intents - Intents.nonPrivileged + Intent.GuildMembers
        }

        kord.listeners()

        log.info { "Starting Franziska" }

        registerCommands()

        log.info { "Logging in" }
        kord.login {
            listening(config.franziska.status)
        }
    }

    private suspend fun registerCommands() {

    }
}