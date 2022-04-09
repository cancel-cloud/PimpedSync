package de.cancelcloud.commands

import de.cancelcloud.PimpedCache
import de.cancelcloud.PimpedSync
import de.cancelcloud.database.InventoryContent
import de.cancelcloud.utils.Base64
import de.jet.jvm.extension.tryOrNull
import de.jet.paper.extension.display.notification
import de.jet.paper.extension.display.ui.buildPanel
import de.jet.paper.extension.display.ui.item
import de.jet.paper.extension.display.ui.skull
import de.jet.paper.extension.paper.*
import de.jet.paper.extension.tasky.async
import de.jet.paper.extension.tasky.sync
import de.jet.paper.structure.command.InterchangeUserRestriction
import de.jet.paper.structure.command.StructuredInterchange
import de.jet.paper.structure.command.completion.buildInterchangeStructure
import de.jet.paper.structure.command.completion.component.CompletionAsset
import de.jet.paper.tool.display.message.Transmission
import de.jet.paper.tool.timing.tasky.TemporalAdvice
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * PERMISSION = PimpedSync.interchange.invsee
 */

class InvseeInterChange : StructuredInterchange("invsee", buildInterchangeStructure {


    concludedExecution {
        "§7This §a§lPimpedSync§7 App from §6Cancelcloud§7 was developed with §eJET Framework§7!"
            .notification(Transmission.Level.INFO, executor)
            .display()
    }

    val completionAsset = CompletionAsset(
        PimpedSync.instance,
        "PimpedSync",
        true,
        //ist die eingabe richtig? => ob der name in der liste enthalten ist
        check = {input, ignoreCase -> (server.onlinePlayers.map { it.name } + InventoryContent.getAllPlayerNames()).any { it.equals(input, ignoreCase)}},
        //transformiert die Eingabe zu einem Objekt (Spielername => Spieler)
        transformer = { input: String -> getPlayer(input) },
        //welche Eingabe soll die Liste anzeigen
        generator = { server.onlinePlayers.map { it.name } + InventoryContent.getAllPlayerNames()}
    )

    branch {
        addContent("cache")

        branch {
            addContent("clear", "update")

            concludedExecution {
                if (getInput(1) == "clear") {
                    PimpedCache.dataBasePlayers = emptyList()
                    "§7Cache cleared.".notification(Transmission.Level.INFO, executor).display()
                } else {
                    InventoryContent.getAllPlayerNames()
                    "§7Cache updated.".notification(Transmission.Level.INFO, executor).display()
                }
            }
        }
    }


    branch {
        addContent(completionAsset)
        concludedExecution {
            @OptIn(ExperimentalTime::class)
            measureTime {
            val executor = this.executor as Player
            val player = tryOrNull { InventoryContent.PlayerData(getInput(0, completionAsset).uniqueId, getInput(0, completionAsset).name,
                Base64.itemStackArrayToBase64(getInput(0, completionAsset).inventory.contents as Array<ItemStack>)!!
            ) } ?: InventoryContent.getPlayerData(getInput(0))
            val inventory = buildPanel(6, false) {
                this.label = Component.text("§a§lInventory of ${player!!.name}")
                this.identity = PimpedSync.identity
                this.icon = skull(player.user)
                //set pannel contents
                Base64.itemStackArrayFromBase64(player.inventory).forEachIndexed { index, itemStack ->
                    if (itemStack != null){
                        //index bezieht sich auf das Pannel
                        this[index + 9] = itemStack
                    }
                }
                set(0..8, Material.GRAY_STAINED_GLASS_PANE.item {
                    blankLabel()
                })

                //If player is online:
                onClick {
                    val user = getPlayer(player.user)
                    if (user != null) {
                        async(TemporalAdvice.Companion.delayed(.1.seconds)) {
                            sync {
                                user.inventory.contents = it.inventoryView.topInventory.contents!!.drop(9).toTypedArray()
                            }
                        }
                    }
                }

                //If player is offline
                onClose {
                    if (getPlayer(player.user) == null) {
                        InventoryContent.dbRequestOfflinePlayer(getOfflinePlayer(player.name),
                            it.inventory.contents?.drop(9)?.toTypedArray() as Array<ItemStack>
                        )
                    }
                }

            }
            inventory.display(executor)
            }.let { time ->
                "§7Inventory of §a${getInput(0)}§7 loaded in §a${time}§7.".notification(Transmission.Level.INFO, executor).display()
            }


        }
    }

}, userRestriction = InterchangeUserRestriction.ONLY_PLAYERS)