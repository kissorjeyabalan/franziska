package dev.lysithea.franziska.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import dev.lysithea.franziska.config.spec.BotSpec
import dev.lysithea.franziska.config.spec.SentrySpec
import java.io.File

class FranziskaConfig {
    private var config = Config {
        addSpec(BotSpec)
        addSpec(SentrySpec)
    }.from.yaml.file("config.yaml")

    init {
        if (File("config.dev.yaml").exists()) {
            config = config.from.yaml.watchFile("config.dev.yaml")
        }
    }

    val environment: String get() = config[SentrySpec.environment]
    val sentryDsn: String get() = config[SentrySpec.dsn]

    val discordToken: String get() = config[BotSpec.token]
    val defaultPrefix: String get() = config[BotSpec.commandPrefix]
}

val config = FranziskaConfig()
