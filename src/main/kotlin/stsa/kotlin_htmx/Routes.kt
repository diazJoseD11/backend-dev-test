package stsa.kotlin_htmx

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import org.slf4j.LoggerFactory
import stsa.kotlin_htmx.link.pages.LinkMainPage
import stsa.kotlin_htmx.models.UserSession
import stsa.kotlin_htmx.pages.*

private val logger = LoggerFactory.getLogger("stsa.kotlin_htmx.Routes")

fun Application.configurePageRoutes() {
    routing {
        // Página principal
        get {
            call.respondHtmlTemplate(MainTemplate(template = EmptyTemplate(), pageTitle = "Front page")) {
                mainSectionTemplate {
                    emptyContentWrapper {
                        section {
                            p { +"Startrack Demo" }
                        }
                    }
                }
            }
        }

        // Ruta de ejemplo /link
        route("/link") {
            val linkMainPage = LinkMainPage()
            get {
                linkMainPage.renderMainPage(this)
            }
        }

        // Ruta de lista de Skins
        get("/skins") {
            call.respondHtmlTemplate(MainTemplate(template = EmptyTemplate(), pageTitle = "Lista de Skins")) {
                mainSectionTemplate {
                    emptyContentWrapper {
                        renderSkinsPage()
                    }
                }
            }
        }

        get("/skins/search") {
            call.respondHtml {
                body {
                    renderSkinsList(call)
                }
            }
        }

        get("/skins/export/xml") {
            val query = call.parameters["query"]?.lowercase()?.trim()

            val skins = if (!query.isNullOrBlank()) {
                stsa.kotlin_htmx.services.SkinService.searchSkins(query)
            } else {
                emptyList()
            }

            call.respondText(
                contentType = io.ktor.http.ContentType.Application.Xml,
                text = buildString {
                    appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
                    appendLine("<skins>")
                    for (skin in skins) {
                        appendLine("  <skin>")
                        appendLine("    <id>${skin.id}</id>")
                        appendLine("    <name>${skin.name}</name>")
                        appendLine("    <image>${skin.image}</image>")
                        appendLine("  </skin>")
                    }
                    appendLine("</skins>")
                }
            )
        }


        // Ruta de lista de Agentes
        get("/agents") {
            call.respondHtmlTemplate(MainTemplate(template = EmptyTemplate(), pageTitle = "Lista de Agentes")) {
                mainSectionTemplate {
                    emptyContentWrapper {
                        renderAgentsPage()
                    }
                }
            }
        }

        get("/agents/search") {
            call.respondHtml {
                body {
                    renderAgentsList(call)
                }
            }
        }

        // Crates
        get("/crates") {
            call.respondHtmlTemplate(MainTemplate(template = EmptyTemplate(), pageTitle = "Lista de Crates")) {
                mainSectionTemplate {
                    emptyContentWrapper {
                        renderCratesPage()
                    }
                }
            }
        }

        get("/crates/search") {
            call.respondHtml {
                body {
                    renderCratesList(call)
                }
            }
        }

        // Página protegida de keys (se revisará sesión luego)
        get("/keys") {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondRedirect("/login")
                return@get
            }

            call.respondHtmlTemplate(MainTemplate(template = EmptyTemplate(), pageTitle = "Lista de Keys")) {
                mainSectionTemplate {
                    emptyContentWrapper {
                        renderKeysPage()
                    }
                }
            }
        }

        get("/keys/search") {
            call.respondHtml {
                body {
                    renderKeysList(call)
                }
            }
        }

        //  Login form
        get("/login") {
            call.respondHtml {
                body {
                    renderLoginForm()
                }
            }
        }

        // Manejo POST del login
        post("/login") {
            val params = call.receiveParameters()
            val username = params["username"]
            val password = params["password"]

            if (username == "admin" && password == "1234") {
                call.sessions.set(UserSession(username = username))
                call.respondRedirect("/keys")
            } else {
                call.respondHtml {
                    body {
                        renderLoginForm(error = true)
                    }
                }
            }
        }

        // Logout
        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }
    }
}
