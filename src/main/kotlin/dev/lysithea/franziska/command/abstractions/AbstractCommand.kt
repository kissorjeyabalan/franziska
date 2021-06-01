package dev.lysithea.franziska.command.abstractions

import dev.lysithea.franziska.command.CommandCategory
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.core.permission.UsageArea

abstract class AbstractCommand {
    open val callback = Exception()

    abstract val name: String
    abstract val description: String
    abstract val permission: PermissionLevel
    abstract val category: CommandCategory
    open val usageArea: UsageArea = UsageArea.ANY
}