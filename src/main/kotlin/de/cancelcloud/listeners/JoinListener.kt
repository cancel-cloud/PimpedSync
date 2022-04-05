package de.cancelcloud.listeners

import de.cancelcloud.database.InventoryContent
import de.cancelcloud.utils.Base64
import de.jet.jvm.extension.isNull
import de.jet.paper.structure.app.event.EventListener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener : EventListener() {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player


        //check if player exists in database
        if(InventoryContent.getPlayerData(player.uniqueId)?.user.isNull) {
            InventoryContent.dbRequestPlayer(player, "insert")
        }

        player.inventory.contents =
            Base64.itemStackArrayFromBase64(InventoryContent.getPlayerData(player.uniqueId)!!.inventory)
    }
}