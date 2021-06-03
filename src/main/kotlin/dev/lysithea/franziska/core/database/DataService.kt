package dev.lysithea.franziska.core.database

import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import dev.lysithea.franziska.core.database.repositories.FranziskaSettingRepository

/**
 * Interface defining available repositories.
 *
 */
interface DataService {
    /**
     * Repository containing [FranziskaSetting]s.
     */
    val settings: FranziskaSettingRepository
}