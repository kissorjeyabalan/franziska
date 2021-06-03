package dev.lysithea.franziska.command.interfaces

import dev.kord.core.entity.Message

interface FranziskaContext {
    suspend fun respond(content: String): Message
}