package dev.lysithea.franziska.commands.administration.settings

import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.command.CommandCategory
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxSubCommand
import dev.lysithea.franziska.core.command.interfaces.SubCommandRegistry
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.core.permission.PermissionState
import dev.lysithea.franziska.core.permission.UsageArea
import dev.lysithea.franziska.dsl.getMentionedUser
import dev.lysithea.franziska.dsl.getUserMention
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsSyntaxCommand : AbstractSyntaxCommand(), SubCommandRegistry<AbstractSyntaxCommand> {
    override val name: String = "settings"
    override val description: String = "adjust server settings"
    override val permission: PermissionLevel = PermissionLevel.ADMIN
    override val category = CommandCategory.ADMINISTRATION
    override val subCommands = mutableMapOf<String, AbstractSyntaxCommand>()
    override val usageArea = UsageArea.GUILD_MESSAGE

    init {
        registerSubCommands(
            ChangePrefixCommand(),
            SetUserRoleCommand()
        )
    }

    override suspend fun execute(context: SyntaxContext) {
        if (!context.args.any()) {
            context.respond(
                Embeds.error(
                    "No subcommand specified",
                    "View available subcommands with `${context.guildSettings.prefix}help ${name}`."
                )
            )
            return
        }

        val subCommand = subCommands[context.args[0].lowercase()]
        if (subCommand != null) {
            subCommand.execute(context.copy(args = context.args.drop(1), command = subCommand))
        } else {
            context.respond(Embeds.error("Command not found", "${context.args[0]} is not a valid subcommand."))
        }
    }

    private inner class SetUserRoleCommand : AbstractSyntaxSubCommand(this), KoinComponent {
        override val name = "setrole"
        override val description = "Assigns role to member. Available roles: ${
            PermissionLevel.values().drop(1).dropLast(1).joinToString(prefix = "`", postfix = "`")
        }"

        override suspend fun execute(context: SyntaxContext) {
            if (context.args.count() != 2 || context.args[0].isBlank() || context.args[1].getUserMention().isBlank()) {
                context.respond(
                    Embeds.error(
                        "Invalid syntax",
                        "Role or member can not be blank or whitespace. Member must be mentioned."
                    )
                )
                return
            }
            val member = context.args[1].getMentionedUser(context.event.getGuild()!!)
            if (member == null) {
                context.respond(Embeds.error("Invalid user", "The specified user does not exist in this server."))
                return
            }

            if (member == context.member) {
                if (!context.isBotOwner(context.member.id.asString)) {
                    context.respond(Embeds.error("Illegal action", "You can not set your own role."))
                    return
                }
            }

            val role = PermissionLevel.values().firstOrNull { it.name.lowercase() == context.args[0].lowercase() }
            if (role == null) {
                context.respond(
                    Embeds.error(
                        "Invalid role",
                        "Valid roles are: ${
                            PermissionLevel.values().drop(1).dropLast(1).joinToString(prefix = "`", postfix = "`")
                        }."
                    )
                )
                return
            }

            when (context.permissionHandler.hasAccess(role, context.member, context.guildSettings)) {
                PermissionState.DECLINED -> {
                    context.respond(
                        Embeds.error(
                            "Insufficient permission",
                            "You can not assign someone a role higher than your own."
                        )
                    )
                    return
                }
                PermissionState.ACCEPTED -> {
                    val dataService by inject<DataService>()
                    val mutableExistingPermissions: MutableMap<String, PermissionLevel> =
                        context.guildSettings.permissions.toMutableMap()
                    mutableExistingPermissions[member.id.asString] = role
                    val updatedSettings = context.guildSettings.copy(permissions = mutableExistingPermissions)
                    dataService.settings.upsert(updatedSettings)
                    context.respond(
                        Embeds.success(
                            "Roles updated",
                            "Successfully assigned the role ${role.name} to ${member.nickname ?: member.displayName}."
                        )
                    )
                    return
                }
                PermissionState.IGNORE -> {
                }
            }
        }

    }

    private inner class ChangePrefixCommand : AbstractSyntaxSubCommand(this), KoinComponent {
        override val name = "prefix"
        override val description = "Changes the prefix Franziska"

        override suspend fun execute(context: SyntaxContext) {
            if (context.args.isEmpty() || context.args[0].isBlank()) {
                context.respond(Embeds.error("Invalid syntax", "Prefix can not be blank or whitespace."))
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
                        "Failed to persist prefix.",
                        "Prefix changed from `${context.guildSettings.prefix}` to `${updatedGuildSettings.prefix}`, but failed to persist. Syntax will change back to previous on next reboot."
                    )
                )
            }
        }

    }
}