package dev.lysithea.franziska

import dev.lysithea.franziska.core.database.DataService
import kotlinx.coroutines.CoroutineScope

interface FranziskaBot : CoroutineScope {
    val initialized: Boolean
    val database: DataService
}