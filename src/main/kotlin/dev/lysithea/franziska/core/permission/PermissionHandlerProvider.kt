package dev.lysithea.franziska.core.permission

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import dev.lysithea.franziska.core.config.Config
import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import mu.KotlinLogging
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
        settings: FranziskaSetting?,
        ignoreBlacklist: Boolean
    ): PermissionState {
        executedBy ?: return PermissionState.DECLINED
        if (executedBy.id.asString == config.franziska.owner)
            return PermissionState.ACCEPTED

        return if (settings == null || !settings.permissions.containsKey(executedBy.id.asString)) {
            if (permission > PermissionLevel.ANY) {
                PermissionState.DECLINED
            } else {
                PermissionState.ACCEPTED
            }
        } else {
            val userPermission = settings.permissions[executedBy.id.asString]!!
            if (userPermission >= permission) {
                PermissionState.ACCEPTED
            } else {
                PermissionState.DECLINED
            }
        }
    }
}