package dev.lysithea.franziska

import dev.lysithea.franziska.core.config.Config
import kotlinx.coroutines.CoroutineScope

/**
 * Interface defining Franziska.
 */
interface FranziskaBot : CoroutineScope {
    /**
     * Whether Franziska is connected to Discord or not.
     */
    val initialized: Boolean
    val config: Config
}