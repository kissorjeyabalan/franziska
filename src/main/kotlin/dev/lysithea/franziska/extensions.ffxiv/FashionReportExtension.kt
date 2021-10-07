package dev.lysithea.franziska.extensions.ffxiv

import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.core.behavior.channel.createEmbed
import dev.lysithea.franziska.services.XivFashionReportService
import org.koin.core.component.inject

class FashionReportExtension : Extension() {
    override val name = "fashion-report"
    private val frService: XivFashionReportService by inject()

    override suspend fun setup() {
        command {
            name = "fr"
            description = "Fetch latest fashion report"

            check { failIf(event.message.author == null) }
            check { event.message.author?.isBot?.let { failIf(it) } }

            action {
                val latestReport = frService.getFashionReport()
                this@command.check { failIf(latestReport == null) }

                latestReport?.let {
                    channel.createEmbed(it.toEmbed())
                }
            }
        }
    }
}
