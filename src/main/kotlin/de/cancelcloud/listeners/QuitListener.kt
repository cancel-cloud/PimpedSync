package de.cancelcloud.listeners

import de.cancelcloud.database.InventoryContent
import de.jet.paper.structure.app.event.EventListener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class QuitListener : EventListener() {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        InventoryContent.dbRequestPlayer(player, "update")

        player.inventory.clear()
    }
}