package dev.lysithea.franziska.core.database

import dev.lysithea.franziska.core.database.repositories.FranziskaSettingRepository

interface DataService {
    val settings: FranziskaSettingRepository
}