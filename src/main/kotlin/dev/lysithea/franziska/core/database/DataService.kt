package dev.lysithea.franziska.core.database

import dev.lysithea.franziska.core.database.entities.FranziskaSetting
import dev.lysithea.franziska.core.database.repositories.FranziskaSettingRepository
import dev.lysithea.franziska.core.database.repositories.XivRepository

/**
 * Interface defining available repositories.
 *
 */
interface DataService {
    /**
     * Repository containing [FranziskaSetting]s.
     */
    val settings: FranziskaSettingRepository

    /**
     * Repository containing xiv related data.
     */
    val xiv: XivRepository
}