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
import stsa.kotlin_htmx.models.CrateDTO
import stsa.kotlin_htmx.models.Crates

object CrateService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun fetchAndSaveCrates() {
        val crates: List<CrateDTO> = client
            .get("https://bymykel.github.io/CSGO-API/api/en/crates.json")
            .body()

        println("Crates recibidas: ${crates.take(1)}...")

        transaction {
            crates.forEach { crate ->
                val exists = Crates.select { Crates.id eq crate.id }.count() > 0
                if (!exists) {
                    Crates.insert {
                        it[id] = crate.id
                        it[name] = crate.name
                        it[image] = crate.image
                    }
                }
            }
        }
    }
}
