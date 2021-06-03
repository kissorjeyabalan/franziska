package dev.lysithea.franziska.command.interfaces

import dev.kord.core.entity.Message
import dev.lysithea.franziska.FranziskaBot

interface FranziskaContext {
    val franziska: FranziskaBot
    suspend fun respond(content: String): Message
}