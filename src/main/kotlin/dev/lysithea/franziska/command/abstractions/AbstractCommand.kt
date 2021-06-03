package dev.lysithea.franziska.command.abstractions

import dev.lysithea.franziska.command.CommandCategory
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.core.permission.UsageArea

/**
 * Base class for all command generators.
 * Not to be used directly. Inherit from [AbstractSyntaxCommand] instead.
 *
 * @property name the name of this command.
 * @property description the description of this command to be used in help.
 * @property permission the minimum level of [PermissionLevel] required to execute this command.
 * @property category the [CommandCategory] to group this command under.
 * @property usageArea the [UsageArea] where this command is permitted to be executed.
 */
abstract class AbstractCommand {

    abstract val name: String
    abstract val description: String
    abstract val permission: PermissionLevel
    abstract val category: CommandCategory
    open val usageArea: UsageArea = UsageArea.ANY
}