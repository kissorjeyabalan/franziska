package dev.lysithea.franziska.external.xiv.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XivApiResponse<T> (
    @SerialName("Pagination")
    val pagination: XivPagination,

    @SerialName("Results")
    val results: List<T>
)

