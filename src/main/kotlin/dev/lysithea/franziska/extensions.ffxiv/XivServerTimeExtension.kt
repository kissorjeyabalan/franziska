package dev.lysithea.franziska.extensions.ffxiv

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed
import dev.lysithea.franziska.ext.BLUE
import dev.lysithea.franziska.ext.format
import dev.lysithea.franziska.util.ffxiv.toEorzeaTime
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import mu.KotlinLogging

class XivServerTimeExtension : Extension() {
    override val name = "xiv-fashion-report"
    val logger = KotlinLogging.logger("xiv")

    override suspend fun setup() {
        chatCommand {
            name = "servertime"
            aliases = arrayOf("st", "et")
            description = "View server time & eorzea time"

            check { failIf(event.message.author == null) }
            check { event.message.author?.isBot?.let { failIf(it) } }

            action {
                val localTime = Clock.System.now().toJavaInstant()
                val eorzeaTime = localTime.toEorzeaTime()

                logger.info { "${eorzeaTime.year} ${eorzeaTime.day} ${eorzeaTime.hour} ${eorzeaTime.minute}" }

                channel.createEmbed {
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
