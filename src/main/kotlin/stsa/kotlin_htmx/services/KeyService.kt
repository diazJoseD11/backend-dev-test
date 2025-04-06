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
import stsa.kotlin_htmx.models.KeyDTO
import stsa.kotlin_htmx.models.Keys

object KeyService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun fetchAndSaveKeys() {
        val keys: List<KeyDTO> = client
            .get("https://bymykel.github.io/CSGO-API/api/en/keys.json")
            .body()

        println("Keys recibidas: ${keys.take(1)}...")

        transaction {
            keys.forEach { key ->
                val exists = Keys.select { Keys.id eq key.id }.count() > 0
                if (!exists) {
                    Keys.insert {
                        it[id] = key.id
                        it[name] = key.name
                        it[image] = key.image
                    }
                }
            }
        }
    }
}
