package stsa.kotlin_htmx.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import stsa.kotlin_htmx.models.Agents
import stsa.kotlin_htmx.models.AgentDTO

object AgentService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun fetchAndSaveAgents() {
        val agents: List<AgentDTO> = client
            .get("https://bymykel.github.io/CSGO-API/api/en/agents.json")
            .body()

        transaction {
            agents.forEach { agent ->
                val exists = Agents.select { Agents.id eq agent.id }.count() > 0
                if (!exists) {
                    Agents.insert {
                        it[id] = agent.id
                        it[name] = agent.name
                        it[image] = agent.image
                        it[team] = agent.team?.name
                    }
                }
            }
        }
    }
}