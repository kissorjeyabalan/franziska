package dev.lysithea.franziska.core.command.abstractions

import dev.lysithea.franziska.core.command.CommandCategory
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.core.permission.UsageArea

abstract class AbstractSyntaxSubCommand(val parent: AbstractSyntaxCommand) : AbstractSyntaxCommand() {
    override val permission: PermissionLevel
        get() = parent.permission
    override val category: CommandCategory
        get() = parent.category
    override val usageArea: UsageArea
        get() = parent.usageArea
}