package dev.lysithea.franziska.commands.general

import dev.lysithea.franziska.core.command.CommandCategory
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.core.permission.UsageArea
import kotlinx.coroutines.launch

class EchoSyntaxCommand : AbstractSyntaxCommand() {
    override val name = "echo"
    override val description: String = "echoes back whatever"
    override val permission: PermissionLevel = PermissionLevel.OWNER
    override val category: CommandCategory = CommandCategory.DEVELOPER
    override val usageArea = UsageArea.ANY

    override suspend fun execute(context: SyntaxContext) {
        context.franziska.launch {
            context.event.message.channel.createMessage(
                context.event.message
                    .content.removePrefix(
                        "${context.guildSettings.prefix}$name"
                    )
            )
        }
    }
}