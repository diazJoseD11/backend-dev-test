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
import stsa.kotlin_htmx.models.*

object SkinService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun fetchAndSaveSkins() {
        val response: List<SkinDTO> = client
            .get("https://bymykel.github.io/CSGO-API/api/en/skins.json")
            .body()

        println("JSON recibido:\n${response.take(1)}...") // Para no saturar logs

        transaction {
            response.forEach { skin ->
                // Verificar si el skin ya existe
                val skinExists = Skins.select { Skins.id eq skin.id }.count() > 0
                if (!skinExists) {
                    Skins.insert {
                        it[id] = skin.id
                        it[name] = skin.name
                        it[description] = skin.description
                        it[team] = skin.team?.name
                        it[image] = skin.image
                    }
                }

                // Insertar crates si no existen y la relación
                skin.crates.forEach { crate ->
                    val crateExists = Crates.select { Crates.id eq crate.id }.count() > 0

                    if (!crateExists) {
                        Crates.insert {
                            it[id] = crate.id
                            it[name] = crate.name
                            it[image] = crate.image
                        }
                    }

                    // Verifica que la relación no exista antes de insertarla
                    val relationExists = SkinCrates.select {
                        (SkinCrates.skinId eq skin.id) and (SkinCrates.crateId eq crate.id)
                    }.count() > 0

                    if (!relationExists) {
                        SkinCrates.insert {
                            it[skinId] = skin.id
                            it[crateId] = crate.id
                        }
                    }
                }
            }
        }
    }

    fun searchSkins(query: String): List<SkinDTO> {
        return transaction {
            Skins
                .select { Skins.name.lowerCase() like "%$query%" }
                .map {
                    SkinDTO(
                        id = it[Skins.id],
                        name = it[Skins.name],
                        image = it[Skins.image]
                    )
                }
        }
    }
}
