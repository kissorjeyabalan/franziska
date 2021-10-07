package dev.lysithea.franziska.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RedditFeed(
    val kind: String,
    val data: FeedData
) {
    @Serializable
    data class FeedData(
        val children: List<FeedItem>
    ) {
        @Serializable
        data class FeedItem(
            val kind: String,
            val data: FeedItemData
        ) {
            @Serializable
            data class FeedItemData(
                val subreddit: String,
                @SerialName("author_fullname") val author: String,
                val title: String,
                val url: String,
                val permalink: String,
                @SerialName("created_utc") val createdAtUtc: Double
            )
        }
    }
}
