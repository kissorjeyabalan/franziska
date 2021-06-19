package dev.lysithea.franziska

import dev.kord.core.Kord
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.gateway.ResumedEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.lysithea.franziska.commands.administration.FeatureSettingsSyntaxCommand
import dev.lysithea.franziska.commands.administration.settings.SettingsSyntaxCommand
import dev.lysithea.franziska.commands.ffxiv.*
import dev.lysithea.franziska.core.command.SyntaxCommandClientProvider
import dev.lysithea.franziska.commands.games.ConnectFourSyntaxCommand
import dev.lysithea.franziska.commands.general.EchoSyntaxCommand
import dev.lysithea.franziska.core.config.Config
import dev.lysithea.franziska.core.permission.PermissionHandlerProvider
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(PrivilegedIntent::class)
class FranziskaBotProvider : FranziskaBot, KoinComponent {
    private val log = KotlinLogging.logger { }
    override val coroutineContext = Dispatchers.IO + SupervisorJob()

    private lateinit var kord: Kord
    override val config by inject<Config>()

    private val syntaxClient =
        SyntaxCommandClientProvider(this, PermissionHandlerProvider())

    override var initialized = false
        private set

    suspend fun start(debug: Boolean = false) {
        kord = Kord(if (!debug) config.franziska.token else config.franziska.devToken) {
            httpClient = HttpClient(CIO)
            intents - Intents.nonPrivileged + Intent.GuildMembers
        }

        kord.listeners()

        log.info { "Starting Franziska" }

        registerCommands()

        log.info { "Logging in" }
        kord.login {
            watching(config.franziska.status)
        }
    }

    private fun Kord.listeners() {
        onReady()
        onDisconnected()
        onResumed()

        with(syntaxClient) {
            onMessageReceived()
        }
    }

    private fun Kord.onReady() = on<ReadyEvent> {
        log.info { "Ready event received. Initializing Franziska." }
        initialized = true
    }

    private fun Kord.onDisconnected() = on<DisconnectEvent> {
        log.info { "Franziska disconnected from Discord." }
        initialized = false
    }

    private fun Kord.onResumed() = on<ResumedEvent> {
        log.info { "Franziska reconnected to Discord. " }
        initialized = true
    }


    private fun registerCommands() {
        syntaxClient.registerCommands(
            EchoSyntaxCommand(),
            ConnectFourSyntaxCommand(),
            SettingsSyntaxCommand(),
            FeatureSettingsSyntaxCommand(),

            // FFXIV
            XivEqUserCommand(),
            XivIamUserCommand(),
            XivTheyAreUserCommand(),
            XivWhoAmICommand(),
            XivWhoIsUserCommand()
        )
    }
}