package dev.lysithea.franziska.core.permission

/**
 * Enumeration defining the result of a permission check.
 */
enum class PermissionState {

    /**
     * Command allowed to be executed.
     */
    ACCEPTED,

    /**
     * User does not have permission to run the command.
     */
    DECLINED,

    /**
     * Command is ignored for the user.
     */
    IGNORE
}