package dev.lysithea.franziska.extensions.admin

import com.kotlindiscord.kord.extensions.DiscordRelayedException
import com.kotlindiscord.kord.extensions.checks.anyGuild
import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.checks.isNotBot
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import dev.kord.common.entity.Permission
import dev.lysithea.franziska.database.entities.GuildSettings
import dev.lysithea.franziska.database.tables.GuildTable
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class SwapPrefixExtension : Extension() {
    override val name = "admin-swap-prefix"
    val logger = KotlinLogging.logger("admin")

    override suspend fun setup() {
        chatCommand(::PrefixArgs) {
            name = "prefix"
            description = "Change command prefix used by Franziska"

            check { isNotBot() }
            check { anyGuild() }
            check { hasPermission(Permission.Administrator) }

            action {
                newSuspendedTransaction {
                    val guildSettings = GuildSettings.findOrCreateById(guild!!.id)
                    if (guildSettings.prefix != arguments.newPrefix) {
                        GuildTable.update({ GuildTable.id eq guildSettings.guildId }) {
                            it[prefix] = arguments.newPrefix
                        }
                    }
                    channel.createMessage(
                        "Prefix has been changed from `${guildSettings.prefix}` to `${arguments.newPrefix}`."
                    )
                }
            }
        }
    }

    inner class PrefixArgs : Arguments() {
        val newPrefix by string("newPrefix", "Prefix you want to use") { arg, value ->
            if (value.isBlank() || value.length > 2) {
                throw DiscordRelayedException("Prefix can not be blank or exceed 2 characters.")
            }
        }
    }
}
