package dev.lysithea.franziska.services

import dev.lysithea.franziska.models.FashionReport
import dev.lysithea.franziska.models.RedditFeed
import io.ktor.client.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class XivFashionReportService : KoinComponent {
    suspend fun getFashionReport(): FashionReport? {
        val httpClient by inject<HttpClient>()

        val redditFeed = httpClient.get<RedditFeed>("https://old.reddit.com/user/kaiyoko/submitted/.json")
        val items = redditFeed.data.children
            .filter { it.data.title.contains("Fashion Report".toRegex(RegexOption.IGNORE_CASE)) }
            .filter { it.data.url.matches("https://i\\.imgur\\.com/.*\\.png".toRegex(setOf(RegexOption.IGNORE_CASE))) }

        return FashionReport.from(items.firstOrNull())
    }
}
