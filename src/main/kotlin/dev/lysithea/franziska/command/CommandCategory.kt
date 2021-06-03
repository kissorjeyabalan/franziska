package dev.lysithea.franziska.command

import dev.lysithea.franziska.command.abstractions.AbstractCommand

/**
 * Available command groups for an [AbstractCommand]
 *
 * @property displayName the name to display in help
 */
enum class CommandCategory(val displayName: String) {
    /**
     * General commands.
     */
    GENERAL("General"),

    /**
     * Commands available to developers.
     */
    DEVELOPER("Developer"),

    /**
     * Dangerous commands.
     */
    DANGER("Danger")
}