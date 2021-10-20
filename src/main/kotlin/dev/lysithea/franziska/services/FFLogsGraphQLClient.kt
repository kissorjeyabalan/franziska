package dev.lysithea.franziska.services

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import dev.lysithea.franziska.config.config
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URL

class FFLogsGraphQLClient(url: URL) : GraphQLKtorClient(url = url), KoinComponent {
    private val httpClient by inject<HttpClient>()
    private var grantedToken: GrantedToken? = null

    override suspend fun <T : Any> execute(
        request: GraphQLClientRequest<T>,
        requestCustomizer: HttpRequestBuilder.() -> Unit
    ): GraphQLClientResponse<T> {
        ensureAuthorization()
        return super.execute(request) {
            header("Authorization", "Bearer ${grantedToken!!.accessToken}")
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun ensureAuthorization() {
        if (grantedToken == null || System.currentTimeMillis() > grantedToken!!.expiresAt) {
            grantedToken = httpClient.submitForm<GrantedToken>(
                url = "https://www.fflogs.com/oauth/token",
                formParameters = Parameters.build {
                    append("grant_type", "client_credentials")
                }
            ) {
                header("Authorization", "Basic ${config.fflogsToken.encodeBase64()}")
            }
        }
    }

    @Serializable
    private data class GrantedToken(
        @SerialName("token_type") val tokenType: String,
        @SerialName("expires_in") val expiresIn: Int,
        @SerialName("access_token") val accessToken: String,
        val expiresAt: Long = System.currentTimeMillis() + expiresIn
    )
}
