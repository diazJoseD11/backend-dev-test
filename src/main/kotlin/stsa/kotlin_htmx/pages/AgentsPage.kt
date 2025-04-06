package stsa.kotlin_htmx.pages

import io.ktor.server.application.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import stsa.kotlin_htmx.models.Agents
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like

fun FlowContent.renderAgentsPage() {
    h1 { +"Agent List" }

    form {
        attributes["hx-get"] = "/agents/search"
        attributes["hx-target"] = "#agents-container"
        attributes["hx-trigger"] = "keyup changed delay:300ms"

        input {
            type = InputType.text
            name = "query"
            placeholder = "Buscar agentes..."
        }
    }

    div {
        id = "agents-container"
    }
}

fun FlowContent.renderAgentsList(call: ApplicationCall) {
    val query = call.parameters["query"]?.lowercase()?.trim()

    val agents = if (!query.isNullOrBlank()) {
        transaction {
            Agents.selectAll()
                .andWhere { Agents.name.lowerCase() like "%$query%" }
                .map {
                    mapOf(
                        "id" to it[Agents.id],
                        "name" to it[Agents.name],
                        "image" to it[Agents.image],
                        "team" to it[Agents.team]
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
                +"Type something to search for agents..."
            }
        }

        agents.isEmpty() -> {
            p {
                style = "color: #666; font-style: italic;"
                +"No agents found matching your search"
            }
        }

        else -> {
            agents.forEach { agent ->
                div {
                    style = "background-color: #fff; padding: 1rem; margin-bottom: 1rem; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.1);"

                    h2 { +"${agent["name"]}" }
                    p { +"Equipo: ${agent["team"] ?: "N/A"}" }

                    img {
                        src = agent["image"].orEmpty()
                        alt = agent["name"].orEmpty()
                        style = "max-width: 200px; margin-top: 0.5rem;"
                    }
                }
            }
        }
    }
}
