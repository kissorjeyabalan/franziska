package dev.lysithea.franziska.di

import dev.lysithea.franziska.services.XivFashionReportService
import org.koin.dsl.module

val _xivModules = module {
    single { XivFashionReportService() }
}
