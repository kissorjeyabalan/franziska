package dev.lysithea.franziska.external.xiv.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XivCharacterResult(
    @SerialName("ID")
    val id: Long,

    @SerialName("Name")
    val name: String,

    @SerialName("Avatar")
    val avatar: String,

    @SerialName("Server")
    val server: String
)