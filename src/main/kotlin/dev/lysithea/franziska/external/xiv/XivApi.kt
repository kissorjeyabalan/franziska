package dev.lysithea.franziska.external.xiv

import dev.lysithea.franziska.core.config.Config
import dev.lysithea.franziska.external.xiv.models.XivApiResponse
import dev.lysithea.franziska.external.xiv.models.XivCharacterResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class XivApi(private val httpClient: HttpClient) : KoinComponent {
    private val config by inject<Config>()
    suspend fun findCharacter(name: String, server: String): XivApiResponse<XivCharacterResult> {
        return httpClient.get(BASE_URL) {
            url {
                path("character", "search")
                parameter("name", name)
                parameter("server", server)
                addXivApiToken()
            }
        }
    }

    private companion object {
        private val BASE_URL = Url("https://xivapi.com")
    }

    private fun HttpRequestBuilder.addXivApiToken(): Unit =
        config.xiv.xivApiToken.let { url.parameters.append("private_key", it) }
}