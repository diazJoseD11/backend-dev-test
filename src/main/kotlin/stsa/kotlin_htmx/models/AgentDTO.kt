package stsa.kotlin_htmx.models

import kotlinx.serialization.Serializable

@Serializable
data class AgentDTO(
    val id: String,
    val name: String,
    val image: String? = null,
    val team: TeamDTO? = null
)