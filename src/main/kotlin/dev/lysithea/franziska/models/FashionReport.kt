package dev.lysithea.franziska.models

data class FashionReport(
    val title: String,
    val url: String
) {
    companion object {
        fun from(redditFeedItem: RedditFeed.FeedData.FeedItem?): FashionReport? {
            if (redditFeedItem == null) return null
            return FashionReport(redditFeedItem.data.title, redditFeedItem.data.url)
        }
    }
}
