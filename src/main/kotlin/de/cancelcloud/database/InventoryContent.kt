package de.cancelcloud.database

import de.cancelcloud.config.GeneralConfig
import de.cancelcloud.utils.Base64
import de.jet.jvm.extension.classType.UUID
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object InventoryContent {


    object InventoryContentTable : Table("inventory_content") {
        val user = uuid("user")
        val name = varchar("name", 255)
        val inventory = text("inventorycontent")

        override val primaryKey = PrimaryKey(user, name)
    }

    data class PlayerData(
        val user: UUID,
        val name: String,
        val inventory: String
    )

    fun createTables() = transaction(database) {
        SchemaUtils.create(InventoryContentTable)
    }

    fun getPlayerData(user: UUID) = transaction(db = database) {
        InventoryContentTable.select { InventoryContentTable.user eq user }.map {
            PlayerData(
                user = it[InventoryContentTable.user],
                name = it[InventoryContentTable.name],
                inventory = it[InventoryContentTable.inventory]
            )
        }.firstOrNull()
    }


    private val database by lazy {
        GeneralConfig.databaseConfig.connect()
    }

    fun dbRequestPlayer(player: Player, mode : String) = transaction(db = database) {
        val user = player.uniqueId
        val name = player.name
        val inventory = Base64.itemStackArrayToBase64(player.inventory.contents as Array<ItemStack>)

        when(mode) {
            "insert"-> {
                transaction(database) {
                    InventoryContentTable.insert {
                        it[InventoryContentTable.user] = user
                        it[InventoryContentTable.name] = name
                        it[InventoryContentTable.inventory] = inventory!!
                    }
                }
            }
            else -> {
                transaction(database) {
                    InventoryContentTable.update {
                        it[InventoryContentTable.user] = user
                        it[InventoryContentTable.name] = name
                        it[InventoryContentTable.inventory] = inventory!!
                    }
                }
            }
        }


    }
}