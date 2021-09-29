package dev.lysithea.franziska.di

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import org.koin.dsl.module

val _httpModules = module {
    // HttpClient
    single { HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json { ignoreUnknownKeys = true })
            install(HttpTimeout)
        }
    } as HttpClient  }
}
