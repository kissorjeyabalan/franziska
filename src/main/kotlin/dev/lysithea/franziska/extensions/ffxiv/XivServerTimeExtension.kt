package dev.lysithea.franziska.extensions.ffxiv

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.Color
import dev.kord.rest.builder.message.create.embed
import dev.lysithea.franziska.config.config
import dev.lysithea.franziska.ext.BLUE
import dev.lysithea.franziska.ext.format
import dev.lysithea.franziska.util.ffxiv.toEorzeaTime
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant

class XivServerTimeExtension : Extension() {
    override val name = "xiv-server-time"

    override suspend fun setup() {
        publicSlashCommand {
            name = "xivtime"
            description = "View server time & eorzea time"

            guild(config.testServer)

            action {
                val localTime = Clock.System.now().toJavaInstant()
                val eorzeaTime = localTime.toEorzeaTime()

                respond {
                    embed {
                        title = "FFXIV Time"
                        color = Color.BLUE
                        description = "Server time: ${localTime.format("HH:mm")}" +
                                "\nEorzea time: ${eorzeaTime.toHourAndMinutes()}"
                        footer {
                            text = "Local time"
                        }
                        timestamp = Clock.System.now()
                    }
                }
            }
        }
    }
}
