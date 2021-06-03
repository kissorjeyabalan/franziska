package dev.lysithea.franziska.core.permission

import dev.kord.core.entity.Member
import dev.lysithea.franziska.core.database.entities.FranziskaSetting

/**
 * Interface defining a handler for permissions.
 *
 */
interface PermissionHandler {

    /**
     * Checks if command [executedBy] [Member](member) fulfills the required [permission].
     *
     * @param permission required [PermissionLevel]
     * @param executedBy [Member](member) to compare.
     * @param ignoreBlacklist if blacklist should be acknowledged.
     * @return
     */
    suspend fun hasAccess(
        permission: PermissionLevel,
        executedBy: Member?,
        settings: FranziskaSetting? = null,
        ignoreBlacklist: Boolean = false
    ): PermissionState
}