package stsa.kotlin_htmx.models

import org.jetbrains.exposed.sql.Table

object Agents : Table("agents") {
    val id = varchar("id", 100)
    val name = varchar("name", 255)
    val image = varchar("image", 500).nullable()
    val team = varchar("team", 100).nullable()

    override val primaryKey = PrimaryKey(id)
}
