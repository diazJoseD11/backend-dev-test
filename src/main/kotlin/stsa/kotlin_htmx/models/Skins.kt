package stsa.kotlin_htmx.models

import org.jetbrains.exposed.sql.Table

object Skins : Table("skins") {
    val id = varchar("id", 50)
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val team = varchar("team", 50).nullable()
    val image = varchar("image", 1024).nullable()

    override val primaryKey = PrimaryKey(id)
}

