package dev.lysithea.franziska.core.permission

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import dev.lysithea.franziska.core.config.Config
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Implementation of [PermissionHandler].
 *
 */
class PermissionHandlerProvider : PermissionHandler, KoinComponent {
    private val config by inject<Config>()

    override suspend fun hasAccess(
        permission: PermissionLevel,
        executedBy: Member?,
        ignoreBlacklist: Boolean
    ): PermissionState {
        executedBy ?: return PermissionState.DECLINED
        if (executedBy.id == Snowflake(config.franziska.owner))
            return PermissionState.ACCEPTED

        return if (permission > PermissionLevel.ANY) {
            PermissionState.DECLINED
        } else {
            PermissionState.ACCEPTED
        }
    }
}