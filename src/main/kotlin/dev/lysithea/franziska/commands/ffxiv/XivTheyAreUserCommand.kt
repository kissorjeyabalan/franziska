package dev.lysithea.franziska.commands.ffxiv

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.withTyping
import dev.lysithea.franziska.constants.Embeds
import dev.lysithea.franziska.core.FranziskaFeature
import dev.lysithea.franziska.core.command.CommandCategory
import dev.lysithea.franziska.core.command.SyntaxContext
import dev.lysithea.franziska.core.command.abstractions.AbstractSyntaxCommand
import dev.lysithea.franziska.core.database.DataService
import dev.lysithea.franziska.core.database.entities.XivUser
import dev.lysithea.franziska.core.permission.PermissionLevel
import dev.lysithea.franziska.dsl.getUserMention
import dev.lysithea.franziska.external.xiv.XivApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class XivTheyAreUserCommand : AbstractSyntaxCommand(), KoinComponent {
    private val xivApi by inject<XivApi>()
    private val dataService by inject<DataService>()

    override val name: String = "theyare"
    override val description = "Links specified character to user"
    override val permission = PermissionLevel.ADMIN
    override val category = CommandCategory.FFXIV

    override suspend fun execute(context: SyntaxContext) {
        if (!context.hasFeature(FranziskaFeature.FFXIV)) return
        val args = Arguments(context.args)
        if (!args.valid) return
        val targetMember = context.event.getGuild()?.getMember(Snowflake(args.userId)) ?: return

        context.channel.withTyping {
            val apiRes = xivApi.findCharacter(args.fullName, args.server)
            if (apiRes.pagination.results == 1L) {
                val foundCharacter = apiRes.results.first()
                dataService.xiv.upsert(
                    XivUser(
                        discordId = targetMember.id.asString,
                        characterName = args.fullName,
                        serverName = args.server,
                        xivId = foundCharacter.id.toString()
                    )
                )
                context.respond(
                    Embeds.thumbnail(
                        foundCharacter.avatar,
                        "Character connected!",
                        "Character ${args.fullName} on ${foundCharacter.server} has been successfully linked to ${targetMember.displayName}."
                    )
                )
            } else {
                context.respond(Embeds.error("No characters found!", "No character named ${args.fullName} found on ${args.server}."))
            }
        }
    }

    private data class Arguments(private val input: List<String>) {
        var valid: Boolean = false
        var userId: String = ""
        var server: String = ""
        var fullName: String = ""

        init {
            if (input.size == 4) {
                userId = input[0].getUserMention()
                server = input[1]
                fullName = "${input[2]} ${input[3]}".also { fullName = it }
                if (userId.isNotBlank() && server.isNotBlank() && fullName.isNotBlank()) {
                    valid = true
                }
            }
        }
    }
}