package dev.lysithea.franziska.commands.ffxiv

import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.reply
import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.FranziskaFeature
import dev.lysithea.franziska.core.command.CommandCategory
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.dsl.getUserMention
import dev.lysithea.franziska.dsl.reply
import dev.lysithea.franziska.external.xiv.XivCardApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class XivWhoIsUserCommand : AbstractSyntaxCommand(), KoinComponent {
    private val xivCardApi by inject<XivCardApi>()
    private val dataService by inject<DataService>()
    override val name = "whois"
    override val description = "displays who a registered user is"
    override val permission = PermissionLevel.ANY
    override val category = CommandCategory.FFXIV

    override suspend fun execute(context: SyntaxContext) {
        if (!context.hasFeature(FranziskaFeature.FFXIV)) return
        if (context.args.size != 1) return
        val mentionedUser = context.args[0].getUserMention()
        if (mentionedUser.isBlank()) return

        context.channel.withTyping {
            val xivUser = dataService.xiv.getOrNull(mentionedUser)
            if (xivUser == null) {
                context.event.message.reply(
                    Embeds.error(
                        "Unknown user!",
                        "This user has not linked their character with Franziska."
                    )
                )
                return@withTyping
            }
            xivCardApi.getInformation(xivUser.xivId).let {
                if (it == null) {
                    context.event.message.reply(
                        Embeds.error("Service error", "Franziska Card Service is currently offline.")
                    )
                    return@withTyping
                }
                context.event.message.reply {
                    addFile("${context.channel.id.asString}.${xivUser.xivId}.png", it)
                }
            }
        }
    }
}