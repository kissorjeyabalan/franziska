package dev.lysithea.franziska.extensions.ffxiv

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.rest.builder.message.EmbedBuilder
import dev.lysithea.franziska.config.config
import dev.lysithea.franziska.services.XivFashionReportService
import org.koin.core.component.inject

class XivFashionReportExtension : Extension() {
    override val name = "xiv-fashion-report"
    private val frService: XivFashionReportService by inject()

    override suspend fun setup() {
        publicSlashCommand {
            name = "fashionreport"
            description = "Fetch latest fashion report"

            guild(config.testServer)

            action {
                val latestReport = frService.getFashionReport()
                this@publicSlashCommand.check { failIf(latestReport == null) }

                respond {
                    if (latestReport != null) {
                        embeds.add(EmbedBuilder().apply(latestReport.toEmbed()))
                    } else {
                        channel.createMessage("Failed to fetch latest fashion report.")
                    }
                }
            }
        }
    }
}
