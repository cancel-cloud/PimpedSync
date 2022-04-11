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
import de.jet.paper.extension.paper.getOfflinePlayer
import de.jet.paper.extension.paper.getPlayer
import de.jet.paper.extension.paper.server
import de.jet.paper.extension.tasky.async
import de.jet.paper.extension.tasky.sync
import de.jet.paper.structure.command.InterchangeUserRestriction
import de.jet.paper.structure.command.StructuredInterchange
import de.jet.paper.structure.command.completion.buildInterchangeStructure
import de.jet.paper.structure.command.completion.component.CompletionAsset
import de.jet.paper.tool.display.message.Transmission
import de.jet.paper.tool.timing.tasky.TemporalAdvice
import de.jet.unfold.extension.asStyledComponent
import de.jet.unfold.extension.asStyledString
import de.jet.unfold.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * PERMISSION = PimpedSync.interchange.invsee
 */

class InvseeInterChange : StructuredInterchange("invsee", buildInterchangeStructure {


    concludedExecution {
        "<gray>This <green><bold>PimpedSync</bold><gray> App from <gold>Cancelcloud<gray> was developed with <yellow>JET Framework<gray>!"
            .notification(Transmission.Level.INFO, executor)
            .display()

        /*
        "<rainbow>REGENBOGEN</rainbow>"
        "Dieser Text hat <gradient:#222:#444>einen Gradient</gradient> hinzugefügt!"

         */

    }

    val completionAsset = CompletionAsset(
        PimpedSync.instance,
        "PimpedSync",
        true,
        //ist die eingabe richtig? => ob der name in der liste enthalten ist
        check = { input, ignoreCase ->
            (server.onlinePlayers.map { it.name } + InventoryContent.getAllPlayerNames()).any {
                it.equals(
                    input,
                    ignoreCase
                )
            }
        },
        //transformiert die Eingabe zu einem Objekt (Spielername => Spieler)
        transformer = { input: String -> getPlayer(input) },
        //welche Eingabe soll die Liste anzeigen
        generator = { server.onlinePlayers.map { it.name } + InventoryContent.getAllPlayerNames() }
    )

    branch {
        addContent("cache")

        branch {
            addContent("clear", "update")

            concludedExecution {
                if (getInput(1) == "clear") {
                    PimpedCache.dataBasePlayers = emptyList()
                    "<gray>Cache cleared<reset>.".notification(Transmission.Level.INFO, executor).display()
                } else {
                    InventoryContent.getAllPlayerNames()
                    "<gray>Cache updated<reset>.".notification(Transmission.Level.INFO, executor).display()
                }
            }
        }
    }


    branch {
        addContent("view")

        branch {
            addContent(completionAsset)
            concludedExecution {
                @OptIn(ExperimentalTime::class)
                measureTime {
                    val executor = this.executor as Player

                    val player = tryOrNull {
                        InventoryContent.PlayerData(
                            getInput(1, completionAsset).uniqueId,
                            getInput(1, completionAsset).name,
                            Base64.itemStackArrayToBase64(
                                getInput(1, completionAsset)
                                    .inventory.contents as Array<ItemStack>
                            )!!
                        )
                    } ?: InventoryContent.getPlayerData(getInput(1))

                    val inventory = buildPanel(6, false) {
                        this.label = "<green><gray>Inventory of ${player!!.name}".asStyledComponent
                        this.identity = PimpedSync.identity
                        this.icon = skull(player.user)
                        //set panel contents
                        Base64.itemStackArrayFromBase64(player.inventory).forEachIndexed { index, itemStack ->
                            if (itemStack != null) {
                                //index bezieht sich auf das Pannel
                                this[index + 9] = itemStack
                            }
                        }
                        set(0..8, Material.GRAY_STAINED_GLASS_PANE.item {
                            blankLabel()
                        })

                        //If player is online:
                        onClick {
                            val user = getOfflinePlayer(player.user)
                            if (user.isOnline) {
                                async(TemporalAdvice.Companion.delayed(.1.seconds)) {
                                    sync {
                                        getPlayer(user.uniqueId)!!.inventory.contents =
                                            it.inventoryView.topInventory.contents!!.drop(9).toTypedArray()
                                    }
                                }
                            }
                        }

                        //If player is offline
                        onClose {
                            if (getPlayer(player.user)?.isOnline == false) {
                                InventoryContent.dbRequestOfflinePlayer(
                                    getOfflinePlayer(player.name),
                                    it.inventory.contents?.drop(9)?.toTypedArray() as Array<ItemStack>
                                )
                            }
                        }

                    }
                    inventory.display(executor)
                }.let { time ->

                    text {

                        text("Inventory of ").color(NamedTextColor.GRAY)
                        text(getInput(1)).color(NamedTextColor.GREEN)
                        text(" loaded in ").color(NamedTextColor.GRAY)
                        text(time.toString(DurationUnit.MILLISECONDS, 2)).color(NamedTextColor.GREEN)
                        text(".").color(NamedTextColor.GRAY)
                        /*Or you could use something like that:
                        text(".").color(TextColor.color(123, 213, 231))
                        text(".").color(TextColor.fromHexString("#222"))

                         */


                    }.notification(Transmission.Level.INFO, executor).display()
                }
            }
        }
    }

}, userRestriction = InterchangeUserRestriction.ONLY_PLAYERS)