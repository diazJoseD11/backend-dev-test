package stsa.kotlin_htmx.models

import org.jetbrains.exposed.sql.Table

object SkinCrates : Table("skincrates") {
    val skinId = varchar("skin_id", 50).references(Skins.id)
    val crateId = varchar("crate_id", 50).references(Crates.id)

    override val primaryKey = PrimaryKey(skinId, crateId)
}

