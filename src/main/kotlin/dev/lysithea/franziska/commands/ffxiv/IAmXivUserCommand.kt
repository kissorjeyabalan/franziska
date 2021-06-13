package dev.lysithea.franziska.commands.ffxiv

import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.entity.ReactionEmoji
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.from
import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.FranziskaFeature
import dev.lysithea.franziska.core.command.CommandCategory
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.database.entities.XivUser
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.external.xivapi.XivApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IAmXivUserCommand : AbstractSyntaxCommand(), KoinComponent {
    private val xivApi by inject<XivApi>()
    private val dataService by inject<DataService>()

    override val name: String = "iam"
    override val description = "Links your character to Franziska"
    override val permission = PermissionLevel.ANY
    override val category = CommandCategory.FFXIV

    override suspend fun execute(context: SyntaxContext) {
        if (!context.hasFeature(FranziskaFeature.FFXIV)) return
        if (context.args.size != 3) {
            context.respond(
                Embeds.error(
                    "Invalid syntax",
                    "Syntax is: `${context.commandWithPrefix} <server> <firstname> <lastname>`."
                )
            )
            return
        }

        val server = context.args[0]
        val name = "${context.args[1]} ${context.args[2]}"

        context.channel.withTyping {
            val res = xivApi.findCharacter(name, server)
            if (res.pagination.results == 1L) {
                val foundCharacter = res.results.first()
                dataService.xiv.upsert(
                    XivUser(
                        discordId = context.member.id.asString,
                        characterName = name,
                        serverName = server,
                        xivId = foundCharacter.id.toString()
                    )
                )
                context.respond(
                    Embeds.thumbnail(
                        foundCharacter.avatar,
                        "Character connected!",
                        "Character $name (${foundCharacter.server}) has been successfully linked to ${context.member.displayName}."
                    )
                )
            } else {
                context.respond(Embeds.error("No characters found!", "No character named $name found on $server."))
            }
        }
    }
}