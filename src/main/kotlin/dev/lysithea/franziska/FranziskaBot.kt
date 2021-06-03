package dev.lysithea.franziska

import dev.kord.core.Kord
import dev.lysithea.franziska.core.database.DataService
import kotlinx.coroutines.CoroutineScope

/**
 * Interface defining Franziska.
 */
interface FranziskaBot : CoroutineScope {
    /**
     * Whether Franziska is connected to Discord or not.
     */
    val initialized: Boolean
}