package dev.lysithea.franziska.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import dev.lysithea.franziska.config.spec.BotSpec
import dev.lysithea.franziska.config.spec.DatabaseSpec
import dev.lysithea.franziska.config.spec.SentrySpec
import dev.lysithea.franziska.config.spec.XivSpec
import java.io.File

class FranziskaConfig {
    private var _config = Config {
        addSpec(BotSpec)
        addSpec(SentrySpec)
        addSpec(DatabaseSpec)
        addSpec(XivSpec)
    }.from.yaml.file("config.yaml")

    val self get() = _config

    init {
        if (File("config.dev.yaml").exists()) {
            _config = _config.from.yaml.watchFile("config.dev.yaml")
        }
    }

    val environment: String get() = self[SentrySpec.environment]
    val sentryDsn: String get() = self[SentrySpec.dsn]

    val discordToken: String get() = self[BotSpec.token]
    val defaultPrefix: String get() = self[BotSpec.commandPrefix]
    val testServer: Long get() = self[BotSpec.testServer]
    val fflogsToken: String get() = self[XivSpec.fflogsToken]
}

val config = FranziskaConfig()
