package stsa.kotlin_htmx.models

import org.jetbrains.exposed.sql.Table

object Crates : Table("crates") {
    val id = varchar("id", 50)
    val name = varchar("name", 255)
    val image = varchar("image", 1024).nullable()

    override val primaryKey = PrimaryKey(id)
}


