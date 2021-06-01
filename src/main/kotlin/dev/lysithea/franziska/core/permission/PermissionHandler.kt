package dev.lysithea.franziska.core.permission

import dev.kord.core.entity.Member


interface PermissionHandler {
    suspend fun hasAccess(
        permission: PermissionLevel,
        executedBy: Member?,
        ignoreBlacklist: Boolean = false
    ): PermissionState
}