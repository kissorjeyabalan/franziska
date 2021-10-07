package dev.lysithea.franziska.ext

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDateTime.format(pattern: String): String = this.format(DateTimeFormatter.ofPattern(pattern))

fun Instant.format(pattern: String): String =
    DateTimeFormatter.ofPattern(pattern).withZone(ZoneOffset.UTC).format(this)
