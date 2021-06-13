package dev.lysithea.franziska.external.xivapi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XivPagination(
    @SerialName("Page")
    val page: Long,

    @SerialName("PageNext")
    val pageNext: Long?,

    @SerialName("PagePrev")
    val pagePrev: Long?,

    @SerialName("PageTotal")
    val pageTotal: Long,

    @SerialName("Results")
    val results: Long,

    @SerialName("ResultsPerPage")
    val resultsPerPage: Long,

    @SerialName("ResultsTotal")
    val resultsTotal: Long
)