package stsa.kotlin_htmx.pages

import io.ktor.server.application.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import stsa.kotlin_htmx.models.Keys

fun FlowContent.renderKeysPage() {
    h1 { +"Lista de Keys" }

    form {
        attributes["hx-get"] = "/keys/search"
        attributes["hx-target"] = "#keys-container"
        attributes["hx-trigger"] = "keyup changed delay:300ms"

        input {
            type = InputType.text
            name = "query"
            placeholder = "Buscar keys..."
        }
    }

    div {
        id = "keys-container"
    }
}

fun FlowContent.renderKeysList(call: ApplicationCall) {
    val query = call.parameters["query"]?.lowercase()?.trim()

    val keys = if (!query.isNullOrBlank()) {
        transaction {
            Keys.selectAll()
                .andWhere { Keys.name.lowerCase() like "%$query%" }
                .map {
                    mapOf(
                        "id" to it[Keys.id],
                        "name" to it[Keys.name],
                        "image" to it[Keys.image]
                    )
                }
        }
    } else {
        emptyList()
    }

    when {
        query.isNullOrBlank() -> {
            p {
                style = "color: #666; font-style: italic;"
                +"Escribe algo para buscar keys..."
            }
        }

        keys.isEmpty() -> {
            p {
                style = "color: #666; font-style: italic;"
                +"No se encontraron keys que coincidan con tu búsqueda."
            }
        }

        else -> {
            keys.forEach { key ->
                div {
                    style = "background-color: #fff; padding: 1rem; margin-bottom: 1rem; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.1);"

                    h2 { +key["name"].orEmpty() }

                    key["image"]?.let {
                        img {
                            src = it
                            alt = key["name"].orEmpty()
                            style = "max-width: 200px; margin-top: 0.5rem;"
                        }
                    }
                }
            }
        }
    }
}
