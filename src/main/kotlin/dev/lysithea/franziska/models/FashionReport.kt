package dev.lysithea.franziska.models

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import dev.lysithea.franziska.ext.BLUE
import dev.lysithea.franziska.ext.format
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class FashionReport(
    val title: String,
    val url: String,
    val createdAtSeconds: Long
) {
    @OptIn(ExperimentalTime::class)
    private val expiryDetail
        get(): Pair<Boolean, String> {
            val postDate = LocalDateTime.ofEpochSecond(createdAtSeconds, 0, ZoneOffset.UTC)
            val expiryDate =
                postDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY)).withHour(8).withMinute(0).withSecond(0)
            val currentDate = LocalDateTime.now()

            if (currentDate.isBefore(expiryDate)) {
                val diff = ChronoUnit.MILLIS.between(currentDate, expiryDate)
                return Duration.milliseconds(diff).toComponents { days, hours, minutes, _, _ ->
                    return@toComponents Pair(false, "Resets in $days days, $hours hours and $minutes minutes.")
                }
            } else {
                val diff = ChronoUnit.MILLIS.between(expiryDate, currentDate)
                return Duration.milliseconds(diff).toComponents { days, hours, minutes, _, _ ->
                    return@toComponents Pair(true, "Expired $days days, $hours hours and $minutes minutes ago.")
                }
            }
        }

    fun toEmbed(): EmbedBuilder.() -> Unit = {
        val (expired, desc) = this@FashionReport.expiryDetail
        color = Color.BLUE
        author {
            name = "Fashion report by kaiyoko"
            url = "https://pbs.twimg.com/profile_images/1408609852805373952/wXEVfyY4_400x400.jpg"
        }
        title = if (expired) "[EXPIRED] " else "" + this@FashionReport.title
        url = this@FashionReport.url
        image = this@FashionReport.url
        description = desc
        footer {
            text = "Posted on ${
                LocalDateTime
                    .ofEpochSecond(createdAtSeconds, 0, ZoneOffset.UTC)
                    .format("dd MM yyyy HH:mm")
            }"
        }
    }

    companion object {
        fun from(redditFeedItem: RedditFeed.FeedData.FeedItem?): FashionReport? {
            if (redditFeedItem == null) return null
            return FashionReport(
                redditFeedItem.data.title,
                redditFeedItem.data.url,
                redditFeedItem.data.createdAtUtc.toLong()
            )
        }
    }
}
