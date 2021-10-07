package dev.lysithea.franziska.util.ffxiv

import java.time.Instant
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalField
import kotlin.math.roundToInt
import kotlin.math.roundToLong

internal const val MINUTES_OF_HOUR = 60
internal const val HOUR_OF_DAY = 24
internal const val DAYS_OF_MONTH = 32
internal const val MONTHS_OF_YEAR = 12
internal const val TIME_RATE = 175.0

data class EorzeaTime(
    val year: Int,
    val day: Int,
    val month: Int,
    val hour: Int,
    val minute: Int
) : TemporalAccessor, Comparable<EorzeaTime> {
    init {
        require(year >= 0)
        require(day in 1..32)
        require(month in 1..12)
        require(hour in 0..23)
        require(minute in 0..59)
    }

    override fun isSupported(temporalField: TemporalField?): Boolean {
        return when (temporalField) {
            ChronoField.YEAR,
            ChronoField.YEAR_OF_ERA,
            ChronoField.DAY_OF_MONTH,
            ChronoField.MONTH_OF_YEAR,
            ChronoField.HOUR_OF_DAY,
            ChronoField.MINUTE_OF_HOUR -> true
            else -> false
        }
    }

    override fun getLong(temporalField: TemporalField?): Long {
        return when (temporalField) {
            ChronoField.YEAR, ChronoField.YEAR_OF_ERA -> year
            ChronoField.DAY_OF_MONTH -> day
            ChronoField.MONTH_OF_YEAR -> month
            ChronoField.HOUR_OF_DAY -> hour
            ChronoField.MINUTE_OF_HOUR -> minute
            else -> error("TemporalField not supported: $temporalField")
        }.toLong()
    }

    override fun compareTo(other: EorzeaTime): Int = toEarthTime().compareTo(other.toEarthTime())

    fun toEarthTime(): Instant {
        val months = MONTHS_OF_YEAR * (year - 1) + (month - 1)
        val days = DAYS_OF_MONTH * months + (day - 1)
        val hours = HOUR_OF_DAY * days + hour
        val minutes = MINUTES_OF_HOUR * hours + minute
        val seconds = (minutes * TIME_RATE / MINUTES_OF_HOUR).roundToLong()

        return Instant.ofEpochSecond(seconds)
    }

    fun toHourAndMinutes(): String {
        return hour.toString()
            .padStart(2, '0') +
                ":" + minute.toString()
            .padStart(2, '0')
    }

    companion object {
        fun from(epochSeconds: Long): EorzeaTime {
            val minutes = (epochSeconds * MINUTES_OF_HOUR / TIME_RATE).roundToInt()
            val hours = minutes / MINUTES_OF_HOUR
            val minute = minutes % MINUTES_OF_HOUR
            val days = hours / HOUR_OF_DAY
            val hour = hours % HOUR_OF_DAY
            val months = days / DAYS_OF_MONTH
            val day = days % DAYS_OF_MONTH + 1
            val year = months / MONTHS_OF_YEAR + 1
            val month = months % MONTHS_OF_YEAR + 1

            return EorzeaTime(year, day, month, hour, minute)
        }

        fun from(time: Instant): EorzeaTime = from(time.epochSecond)

        fun now(): EorzeaTime = from(Instant.now())
    }
}

fun Instant.toEorzeaTime(): EorzeaTime = EorzeaTime.from(this)
