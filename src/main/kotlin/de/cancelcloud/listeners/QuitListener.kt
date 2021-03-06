package de.cancelcloud.listeners

import de.cancelcloud.database.InventoryContent
import de.cancelcloud.database.RequestType.UPDATE
import de.moltenKt.paper.extension.mainLog
import de.moltenKt.paper.structure.app.event.EventListener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class QuitListener : EventListener() {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        @OptIn(ExperimentalTime::class)
        measureTime {

            InventoryContent.databaseAction(player, UPDATE)

            player.inventory.clear()

        }.let {
            mainLog.info(
                "§7[§eQuitListener§7]§r Player §2${player.name}§r has been saved in §a${
                    it.toString(
                        DurationUnit.MILLISECONDS, 2
                    )
                }."
            )
        }

    }
}