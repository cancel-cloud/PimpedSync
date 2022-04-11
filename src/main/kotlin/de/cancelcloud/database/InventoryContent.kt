package de.cancelcloud.database

import de.cancelcloud.config.GeneralConfig
import de.cancelcloud.database.RequestType.INSERT
import de.cancelcloud.database.RequestType.UPDATE
import de.cancelcloud.utils.Base64
import de.jet.jvm.extension.classType.UUID
import de.jet.jvm.extension.forceCast
import org.bukkit.OfflinePlayer
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
        val inventory: String,
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

    fun getPlayerData(name: String) = transaction(db = database) {
        InventoryContentTable.select { InventoryContentTable.name eq name }.map {
            PlayerData(
                user = it[InventoryContentTable.user],
                name = it[InventoryContentTable.name],
                inventory = it[InventoryContentTable.inventory]
            )
        }.firstOrNull().also {
            println("DATA-RECEIVED for '$name' === $it")
        }
    }

    // get only all player names from database
    fun databaseAllNames() = transaction(db = database) {
        InventoryContentTable.selectAll().map {
            it[InventoryContentTable.name]
        }
    }


    private val database by lazy {
        GeneralConfig.databaseConfig.connect()
    }

    fun databaseAction(player: Player, requestType: RequestType) = transaction(db = database) {
        val user = player.uniqueId
        val name = player.name
        val inventory = Base64.itemStackArrayToBase64(player.inventory.contents.forceCast())

        when (requestType) {
            INSERT -> {
                InventoryContentTable.insert {
                    it[InventoryContentTable.user] = user
                    it[InventoryContentTable.name] = name
                    it[InventoryContentTable.inventory] = inventory!!
                }
            }
            UPDATE -> {
                InventoryContentTable.update({ InventoryContentTable.user eq user }) {
                    it[InventoryContentTable.name] = name
                    it[InventoryContentTable.inventory] = inventory!!
                }
            }
        }


    }

    fun databasePush(offlinePlayer: OfflinePlayer, inventorContent: Array<ItemStack>) = transaction(db = database) {
        val user = offlinePlayer.uniqueId
        val name = offlinePlayer.name
        val inventory = Base64.itemStackArrayToBase64(inventorContent)

        InventoryContentTable.update ({ InventoryContentTable.user eq user }) {
            it[InventoryContentTable.name] = name!!
            it[InventoryContentTable.inventory] = inventory!!
        }

    }

}