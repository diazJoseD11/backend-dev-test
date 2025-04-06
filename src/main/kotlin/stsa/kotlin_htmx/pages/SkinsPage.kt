package stsa.kotlin_htmx.pages

import io.ktor.server.application.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.lowerCase
import stsa.kotlin_htmx.models.Skins

// Función para mostrar solo el buscador y el contenedor vacío
fun FlowContent.renderSkinsPage() {
    h1 { +"Lista de skins" }

    form {
        attributes["hx-get"] = "/skins/search"
        attributes["hx-target"] = "#skins-container"
        attributes["hx-trigger"] = "keyup changed delay:300ms"

        input {
            type = InputType.text
            name = "query"
            id = "query-input" // 👈 importante para el script
            placeholder = "Buscar skins..."
        }

        a {
            id = "export-link"
            href = "/skins/export/xml?query=" // valor por defecto vacío
            +"📄 Exportar a XML"
            attributes["target"] = "_blank"
            style = "margin-left: 1rem;" // opcional: separación visual
        }

        script {
            unsafe {
                raw(
                    """
            const queryInput = document.getElementById("query-input");
            const exportLink = document.getElementById("export-link");

            queryInput.addEventListener("input", () => {
                const query = queryInput.value.trim();
                exportLink.href = `/skins/export/xml?query=${'$'}{encodeURIComponent(query)}`;
            });
            """.trimIndent()
                )
            }

            div {
                id = "skins-container"
            }
        }

        // Function that returns the filtered result (fragment that updates the container)
        fun FlowContent.renderSkinsList(call: ApplicationCall) {
            val query = call.parameters["query"]?.lowercase()?.trim()

            val skins = if (!query.isNullOrBlank()) {
                transaction {
                    Skins.selectAll()
                        .andWhere { Skins.name.lowerCase() like "%$query%" }
                        .map {
                            mapOf(
                                "id" to it[Skins.id],
                                "name" to it[Skins.name],
                                "description" to it[Skins.description],
                                "team" to it[Skins.team],
                                "image" to it[Skins.image]
                            )
                        }
                }
            } else {
                emptyList()
            }

            // Visualización de resultados
            when {
                query.isNullOrBlank() -> {
                    p {
                        style = "color: #666; font-style: italic;"
                        +"Type something to search for skins..."
                    }
                }

                skins.isEmpty() -> {
                    p {
                        style = "color: #666; font-style: italic;"
                        +"No skins found matching your search"
                    }
                }

                else -> {
                    skins.forEach { skin ->
                        div {
                            style =
                                "background-color: #fff; padding: 1rem; margin-bottom: 1rem; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.1);"

                            h2 {
                                style = "margin-bottom: 0.5rem;"
                                +"★ ${skin["name"]}"
                            }

                            p {
                                unsafe { +skin["description"].orEmpty() }
                            }

                            p {
                                +"Equipo: ${skin["team"] ?: "N/A"}"
                            }

                            img {
                                src = skin["image"].orEmpty()
                                alt = skin["name"].orEmpty()
                                style = "max-width: 200px; margin-top: 0.5rem;"
                            }
                        }
                    }
                }
            }
        }
    }
}