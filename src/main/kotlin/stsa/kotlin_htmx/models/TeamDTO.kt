package stsa.kotlin_htmx.models

import kotlinx.serialization.Serializable

@Serializable
data class TeamDTO(
    val id: String,
    val name: String
)

