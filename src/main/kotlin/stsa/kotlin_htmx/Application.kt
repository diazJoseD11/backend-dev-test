package stsa.kotlin_htmx

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.runBlocking
import java.io.File
import stsa.kotlin_htmx.plugins.configureHTTP
import stsa.kotlin_htmx.plugins.configureMonitoring
import stsa.kotlin_htmx.plugins.configureRouting
import stsa.kotlin_htmx.services.SkinService
import stsa.kotlin_htmx.services.AgentService
import stsa.kotlin_htmx.services.CrateService
import stsa.kotlin_htmx.services.KeyService
import stsa.kotlin_htmx.models.UserSession

data class ApplicationConfig(
    val lookupApiKey: String
) {
    companion object {
        fun load(): ApplicationConfig {
            System.setProperty("io.ktor.development", "true")

            fun Map<String, String>.envOrLookup(key: String): String {
                return System.getenv(key) ?: this[key]!!
            }

            val envVars: Map<String, String> = envFile().let { envFile ->
                if (envFile.exists()) {
                    envFile.readLines()
                        .map { it.split("=") }
                        .filter { it.size == 2 }
                        .associate { it.first().trim() to it.last().trim() }
                } else emptyMap()
            }

            return ApplicationConfig(
                lookupApiKey = envVars.envOrLookup("LOOKUP_API_KEY")
            )
        }
    }
}

fun envFile(): File {
    return listOf(".env.local", ".env.default").map { File(it) }.first { it.exists() }
}

fun main() {
    if (envFile().readText().contains("KTOR_DEVELOPMENT=true")) {
        System.setProperty("io.ktor.development", "true")
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.httpOnly = true
        }
    }
}

fun Application.module() {
    configureHTTP()
    configureMonitoring()
    configureRouting()
    configureSecurity()
    install(Compression)

    val config = ApplicationConfig.load()

    DatabaseFactory.init()

    runBlocking {
        SkinService.fetchAndSaveSkins()
        AgentService.fetchAndSaveAgents()
        CrateService.fetchAndSaveCrates()
        KeyService.fetchAndSaveKeys()
    }

    configurePageRoutes()
}
