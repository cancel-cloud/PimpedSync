package de.cancelcloud.listeners

import de.cancelcloud.database.InventoryContent
import de.cancelcloud.utils.Base64
import de.jet.jvm.extension.isNull
import de.jet.paper.extension.mainLog
import de.jet.paper.structure.app.event.EventListener
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class JoinListener : EventListener() {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        event.joinMessage(Component.text(""))
        @OptIn(ExperimentalTime::class)
        measureTime {
            //check if player exists in database
            if(InventoryContent.getPlayerData(player.uniqueId)?.user.isNull) {
                InventoryContent.dbRequestPlayer(player, "insert")
            }

            player.inventory.contents =
                Base64.itemStackArrayFromBase64(InventoryContent.getPlayerData(player.uniqueId)!!.inventory)
        }.let {
            mainLog.info("§7[§eJoinListener§7]§r §2${player.name}§r joined the server. Fetched inventory in: §a${it.toString(DurationUnit.MILLISECONDS, 2)}")
        }
    }
}