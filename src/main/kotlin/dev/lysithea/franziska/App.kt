package dev.lysithea.franziska

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.kord.common.entity.Snowflake
import dev.lysithea.franziska.config.buildInfo
import dev.lysithea.franziska.config.config
import dev.lysithea.franziska.config.spec.DatabaseSpec
import dev.lysithea.franziska.database.entities.GuildSettings
import dev.lysithea.franziska.database.tables.GuildTable
import dev.lysithea.franziska.database.tables.XivUserTable
import dev.lysithea.franziska.di._httpModules
import dev.lysithea.franziska.di._xivModules
import dev.lysithea.franziska.extensions.admin.SwapPrefixExtension
import dev.lysithea.franziska.extensions.ffxiv.XivFashionReportExtension
import dev.lysithea.franziska.extensions.ffxiv.XivServerTimeExtension
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

lateinit var bot: ExtensibleBot
suspend fun main() {
    addShutdownHook()
    val logger = KotlinLogging.logger("main")
    logger.info { "Connecting to database" }
    connectToDatabase()

    bot = ExtensibleBot(config.discordToken) {
        chatCommands {
            enabled = true
            defaultPrefix = config.defaultPrefix
            prefix {
                getGuildPrefix(guildId)
            }
        }
        extensions {
            sentry {
                if (config.sentryDsn.isNotBlank()) {
                    enable = true
                    dsn = config.sentryDsn
                    environment = config.environment
                }
            }

            add(::XivFashionReportExtension)
            add(::XivServerTimeExtension)
            add(::SwapPrefixExtension)
        }
    }

    bot.getKoin().loadModules(
        listOf(
            _httpModules,
            _xivModules
        )
    )

    logger.info { "Starting Franziska v${buildInfo.version}" }

    bot.start()
}

private suspend fun getGuildPrefix(snowflake: Snowflake?): String {
    if (snowflake == null) return config.defaultPrefix
    return newSuspendedTransaction {
        return@newSuspendedTransaction GuildSettings.findOrCreateById(snowflake).prefix
    }
}

private lateinit var dataSource: HikariDataSource
private suspend fun connectToDatabase() {
    val dbConfig = HikariConfig().apply {
        jdbcUrl =
            "jdbc:postgresql://${config.self[DatabaseSpec.host]}:${config.self[DatabaseSpec.port]}" +
                    "/${config.self[DatabaseSpec.database]}"
        username = config.self[DatabaseSpec.username]
        password = config.self[DatabaseSpec.password]
        maximumPoolSize = config.self[DatabaseSpec.poolSize]
    }
    dataSource = HikariDataSource(dbConfig)
    Database.connect(dataSource)

    newSuspendedTransaction {
        SchemaUtils.createMissingTablesAndColumns(
            GuildTable,
            XivUserTable
        )
        exec("SELECT * FROM pg_extension WHERE extname = 'pg_trgm'") { rs ->
            require(rs.next()) { "pg_tgrm extension is required." }
        }
    }
}

private fun addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            KotlinLogging.logger("main").info { "Gracefully shutting down" }
            dataSource.close()
        }
    })
}
