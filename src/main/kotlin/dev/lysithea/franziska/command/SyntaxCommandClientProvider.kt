package dev.lysithea.franziska.command

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.lysithea.franziska.FranziskaBot
import dev.lysithea.franziska.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.command.interfaces.CommandClient
import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.permission.PermissionHandler
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.core.permission.PermissionState
import dev.lysithea.franziska.dsl.createMessage
import dev.lysithea.franziska.utils.DefaultThreadFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class SyntaxCommandClientProvider(
    override val permissionHandler: PermissionHandler,
    override val executor: CoroutineContext = Executors.newFixedThreadPool(
        5,
        DefaultThreadFactory("CommandClient")
    ).asCoroutineDispatcher()
) : CommandClient<SyntaxContext, AbstractSyntaxCommand>, KoinComponent
{   private val franziska by inject<FranziskaBot>()
    private val log = KotlinLogging.logger { }
    override val errorHandler = DebugErrorHandler<SyntaxContext>()

    override val commands = mutableMapOf<String, AbstractSyntaxCommand>()

    override fun Kord.onMessageReceived(): Job = on<MessageCreateEvent> { dispatchCommand(this) }

    private suspend fun dispatchCommand(event: MessageCreateEvent) {
        if (!franziska.initialized) return
        if (event.message.author?.isBot == true) return
        return parseCommand(event)
    }

    private suspend fun parseCommand(event: MessageCreateEvent) {
        val guild = event.getGuild() ?: return
        val member = event.member ?: return
        val guildSettings = franziska.database.settings.getOrDefault(guild.id)
        if (!guildSettings.enabled) return

        val commandItems = event.message.content.split("\\s+".toRegex())

        if (event.message.content.startsWith(guildSettings.prefix)) {
            val command = resolveCommand(commandItems[0].removePrefix(guildSettings.prefix)) ?: return
            when (permissionHandler.hasAccess(command.permission, member)) {
                PermissionState.ACCEPTED -> {
                    if (!command.usageArea.matches(event)) return handleIncorrectUsageArea(event.message.channel)
                    val args = commandItems.drop(1)
                    val context = SyntaxContext(guildSettings.prefix, permissionHandler, command, args, member, event)
                    processCommand(command, context)
                }
                PermissionState.DECLINED -> return handleInsufficientPermission(command.permission, event.message.channel.asChannel())
                PermissionState.IGNORE -> return
            }
        }
    }

    private fun processCommand(command: AbstractSyntaxCommand, context: SyntaxContext) {
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            errorHandler.handleException(throwable, context, Thread.currentThread(), coroutineContext)
        }

        franziska.launch(executor + exceptionHandler) {
            log.info { "SyntaxCommand $command was executed by ${context.member}." }
            command.execute(context)
        }
    }

    private fun resolveCommand(cmd: String): AbstractSyntaxCommand? {
        return commands[cmd.lowercase()]
    }

    private suspend fun handleIncorrectUsageArea(channel: MessageChannelBehavior) {
        channel.createMessage(
            Embeds.error(
                "Invalid context.",
                "This command can not be used in this context."
            )
        )
    }

    private suspend fun handleInsufficientPermission(permission: PermissionLevel, channel: MessageChannelBehavior) {
        channel.createMessage(
            Embeds.error(
                "Insufficient permission",
                "You must at least have the $permission permission to execute this command."
            )
        )
    }

    // Intentional null
    override fun Kord.onInteraction(): Job = null!!
}