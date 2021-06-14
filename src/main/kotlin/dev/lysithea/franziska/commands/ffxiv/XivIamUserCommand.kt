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
import dev.lysithea.franziska.external.xiv.XivApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class XivIamUserCommand : AbstractSyntaxCommand(), KoinComponent {
    private val xivApi by inject<XivApi>()
    private val dataService by inject<DataService>()

    override val name: String = "iam"
    override val description = "Links your character to Franziska"
    override val permission = PermissionLevel.ANY
    override val category = CommandCategory.FFXIV

    override suspend fun execute(context: SyntaxContext) {
        if (!context.hasFeature(FranziskaFeature.FFXIV)) return
        val args = Arguments(context.args)
        if (!args.valid) {
            context.respond(
                Embeds.error(
                    "Invalid syntax",
                    "Syntax is: `${context.commandWithPrefix} <server> <firstname> <lastname>`."
                )
            )
            return
        }

        if (args.delete) {
            context.channel.withTyping {
                dataService.xiv.delete(context.memberId)
                context.event.message.addReaction(ReactionEmoji.from(Emojis.whiteCheckMark))
            }
            return
        }

        context.channel.withTyping {
            val res = xivApi.findCharacter(args.fullName, args.server)
            if (res.pagination.results == 1L) {
                val foundCharacter = res.results.first()
                dataService.xiv.upsert(
                    XivUser(
                        discordId = context.member.id.asString,
                        characterName = args.fullName,
                        serverName = args.server,
                        xivId = foundCharacter.id.toString()
                    )
                )
                context.respond(
                    Embeds.thumbnail(
                        foundCharacter.avatar,
                        "Character connected!",
                        "Character ${args.fullName} on ${foundCharacter.server} has been successfully linked to ${context.member.displayName}."
                    )
                )
            } else {
                context.respond(Embeds.error("No characters found!", "No character named ${args.fullName} found on ${args.server}."))
            }
        }
    }

    private data class Arguments(private val input: List<String>) {
        var valid: Boolean = false
        var delete: Boolean = false
        var server: String = ""
        var fullName: String = ""

        init {
            if (input.size == 1 && input.first().lowercase() == "dead") {
                valid = true
                delete = true
            }
            if (input.size == 3) {
                server = input[0]
                "${input[1]} ${input[2]}".also { fullName = it }
                valid = true
            }
        }
    }
}