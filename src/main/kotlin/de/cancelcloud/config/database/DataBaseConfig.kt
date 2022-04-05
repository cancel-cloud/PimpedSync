package de.cancelcloud.config.database

import org.jetbrains.exposed.sql.Database

@kotlinx.serialization.Serializable
data class DataBaseConfig(
    val url: String = "jdbc:postgresql://127.0.0.1:5432/monocraft",
    val driver: String = "com.mysql.jdbc.Driver",
    val user: String = "root",
    val password: String = "",
) {

    fun connect() = Database.connect(url, driver, user, password)

}