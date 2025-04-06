package stsa.kotlin_htmx.models

import kotlinx.serialization.Serializable

@Serializable
data class SkinDTO(
    val id: String,
    val name: String,
    val description: String? = null,
    val image: String? = null,
    val team: TeamDTO? = null,
    val crates: List<CrateDTO> = emptyList()
)
