package stsa.kotlin_htmx.pages

import io.ktor.server.application.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import stsa.kotlin_htmx.models.Crates

fun FlowContent.renderCratesPage() {
    h1 { +"Lista de Crates" }

    form {
        attributes["hx-get"] = "/crates/search"
        attributes["hx-target"] = "#crates-container"
        attributes["hx-trigger"] = "keyup changed delay:300ms"

        input {
            type = InputType.text
            name = "query"
            placeholder = "Buscar crates..."
        }
    }

    div {
        id = "crates-container"
    }
}

fun FlowContent.renderCratesList(call: ApplicationCall) {
    val query = call.parameters["query"]?.lowercase()?.trim()

    val crates = if (!query.isNullOrBlank()) {
        transaction {
            Crates.selectAll()
                .andWhere { Crates.name.lowerCase() like "%$query%" }
                .map {
                    mapOf(
                        "id" to it[Crates.id],
                        "name" to it[Crates.name],
                        "image" to it[Crates.image]
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
                +"Type something to search for crates..."

            }
        }

        crates.isEmpty() -> {
            p {
                style = "color: #666; font-style: italic;"
                +"No crates found matching your search"
            }
        }

        else -> {
            crates.forEach { crate ->
                div {
                    style = "background-color: #fff; padding: 1rem; margin-bottom: 1rem; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.1);"

                    h2 { +crate["name"].orEmpty() }

                    crate["image"]?.let {
                        img {
                            src = it
                            alt = crate["name"].orEmpty()
                            style = "max-width: 200px; margin-top: 0.5rem;"
                        }
                    }
                }
            }
        }
    }
}
