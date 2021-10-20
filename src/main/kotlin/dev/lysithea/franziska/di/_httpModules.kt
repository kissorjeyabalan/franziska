package dev.lysithea.franziska.di

import dev.lysithea.franziska.services.FFLogsGraphQLClient
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import org.koin.dsl.module
import java.net.URL

val _httpModules = module {
    // HttpClient
    single {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json { ignoreUnknownKeys = true })
                install(HttpTimeout)
            }
        }
    }

    // GraphQLClient for FFLogs
    single { FFLogsGraphQLClient(URL("https://www.fflogs.com/api/v2/client")) }
}
