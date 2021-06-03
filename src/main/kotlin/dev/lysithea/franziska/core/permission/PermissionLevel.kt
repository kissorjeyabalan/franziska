@file:Suppress("unused")
package dev.lysithea.franziska.core.permission

/**
 * Enumerations of available permissions.
 * Ordered from lowest to highest.
 * Highest has access to lower.
 */
enum class PermissionLevel {
    /**
     * Anyone can execute.
     */
    ANY,

    /**
     * Moderators can execute.
     */
    MOD,

    /**
     * Administrators can execute.
     */
    ADMIN,

    /**
     * Bot owner can execute.
     */
    OWNER
}