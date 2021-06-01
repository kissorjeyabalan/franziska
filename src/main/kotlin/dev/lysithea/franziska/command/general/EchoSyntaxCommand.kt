package dev.lysithea.franziska.command.general

import dev.lysithea.franziska.command.CommandCategory
import dev.lysithea.franziska.command.SyntaxContext
import dev.lysithea.franziska.command.abstractions.AbstractSyntaxCommand
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
                        "${context.prefix}$name"
                    )
            )
        }
    }
}