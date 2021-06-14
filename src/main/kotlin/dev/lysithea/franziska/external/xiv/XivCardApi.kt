package dev.lysithea.franziska.external.xiv

import dev.lysithea.franziska.core.config.Config
import io.ktor.client.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.InputStream

class XivCardApi(private val httpClient: HttpClient) : KoinComponent {
    private val config by inject<Config>()

    suspend fun getInformation(xivId: String): InputStream? {
        return httpClient.get(config.xiv.cardServiceUri) {
            url {
                path("characters", "id", "$xivId.png")
            }
        }
    }

    suspend fun getEquipments(xivId: String): InputStream? {
        return httpClient.get(config.xiv.cardServiceUri) {
            url {
                path("characters", "equipments", "id", "$xivId.png")
            }
        }
    }
}