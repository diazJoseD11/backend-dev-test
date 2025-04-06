package stsa.kotlin_htmx

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import stsa.kotlin_htmx.models.Skins
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import stsa.kotlin_htmx.models.Crates
import stsa.kotlin_htmx.models.SkinCrates
import stsa.kotlin_htmx.models.Agents
import stsa.kotlin_htmx.models.Keys




object DatabaseFactory {
    fun init() {
        println("Conectando a PostgreSQL...")

        val dbUrl = "jdbc:postgresql://${System.getenv("DB_HOST")}:${System.getenv("DB_PORT")}/${System.getenv("DB_NAME")}"
        val user = System.getenv("DB_USER")
        val password = System.getenv("DB_PASSWORD")

        var connected = false
        var attempts = 0

        while (!connected && attempts < 10) {
            try {
                Database.connect(
                    url = dbUrl,
                    driver = "org.postgresql.Driver",
                    user = user,
                    password = password
                )
                connected = true
                println(" Conectado a PostgreSQL")
            } catch (e: Exception) {
                attempts++
                println(" Fallo conexión intento $attempts - Esperando 3 segundos...")
                Thread.sleep(3000)
            }
        }

        if (!connected) {
            error("No se pudo conectar a PostgreSQL luego de varios intentos.")
        }

        transaction {
            addLogger(Slf4jSqlDebugLogger)
            println("Creando tablas si no existen...")
            SchemaUtils.create(Skins, Crates, SkinCrates, Agents, Keys)
        }
    }
}

