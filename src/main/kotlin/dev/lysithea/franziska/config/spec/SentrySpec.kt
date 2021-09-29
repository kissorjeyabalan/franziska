package dev.lysithea.franziska.config.spec

import com.uchuhimo.konf.ConfigSpec
import dev.kord.common.entity.optional.optional

object SentrySpec : ConfigSpec() {
    val environment by required<String>(description = "application environment")
    val dsn by optional<String>("", description = "sentry dsn")
}
