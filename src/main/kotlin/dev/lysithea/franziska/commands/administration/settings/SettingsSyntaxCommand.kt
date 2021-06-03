package dev.lysithea.franziska.commands.administration.settings

import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.command.CommandCategory
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxSubCommand
import dev.lysithea.franziska.core.command.interfaces.SubCommandRegistry
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.permission.PermissionLevel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsSyntaxCommand : AbstractSyntaxCommand(), SubCommandRegistry<AbstractSyntaxCommand> {
    override val name: String = "settings"
    override val description: String = "adjust server settings"
    override val permission: PermissionLevel = PermissionLevel.ADMIN
    override val category = CommandCategory.ADMINISTRATION
    override val subCommands = mutableMapOf<String, AbstractSyntaxCommand>()

    init {
        registerSubCommands(
            ChangePrefixCommand()
        )
    }

    override suspend fun execute(context: SyntaxContext) {
        if (!context.args.any()) {
            context.respond(
                Embeds.error(
                    "No subcommands specified!",
                    "View available subcommands with `${context.guildSettings.prefix}help ${name}`"
                )
            )
            return
        }

        val subCommand = subCommands[context.args[0].lowercase()]
        if (subCommand != null) {
            subCommand.execute(context.copy(args = context.args.drop(1), command = subCommand))
        } else {
            context.respond(Embeds.error("Command not found!", "${context.args[0]} is not a valid subcommand."))
        }
    }

    private inner class ChangePrefixCommand : AbstractSyntaxSubCommand(this), KoinComponent {
        override val name = "prefix"
        override val description = "Changes the prefix Franziska"

        override suspend fun execute(context: SyntaxContext) {
            if (context.args.isEmpty() || context.args[0].isBlank()) {
                context.respond(Embeds.error("Invalid syntax!", "Prefix can not be blank or whitespace."))
                return
            }

            val dataService by inject<DataService>()
            val updatedGuildSettings = context.guildSettings.copy(prefix = context.args[0])
            if (dataService.settings.upsert(updatedGuildSettings)) {
                context.respond(
                    Embeds.success(
                        "Settings updated!",
                        "Prefix has been changed from `${context.guildSettings.prefix}` to `${updatedGuildSettings.prefix}`"
                    )
                )
            } else {
                context.respond(
                    Embeds.error(
                        "Failed to persist prefix. ",
                        "Prefix changed from `${context.guildSettings.prefix}` to `${updatedGuildSettings.prefix}`, but failed to persist. Syntax will change back to previous on next reboot."
                    )
                )
            }
        }

    }
}