package dev.lysithea.franziska.extensions.ffxiv

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import dev.kord.core.behavior.channel.createEmbed
import dev.lysithea.franziska.services.XivFashionReportService
import org.koin.core.component.inject

class XivFashionReportExtension : Extension() {
    override val name = "xiv-fashion-report"
    private val frService: XivFashionReportService by inject()

    override suspend fun setup() {
        chatCommand {
            name = "fr"
            description = "Fetch latest fashion report"

            check { failIf(event.message.author == null) }
            check { event.message.author?.isBot?.let { failIf(it) } }

            action {
                val latestReport = frService.getFashionReport()
                this@chatCommand.check { failIf(latestReport == null) }

                latestReport?.let {
                    channel.createEmbed(it.toEmbed())
                }
            }
        }
    }
}
