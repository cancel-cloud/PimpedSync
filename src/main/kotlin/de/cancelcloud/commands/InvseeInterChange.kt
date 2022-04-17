package de.cancelcloud.commands

import de.cancelcloud.PimpedCache
import de.cancelcloud.PimpedSync
import de.cancelcloud.database.InventoryContent
import de.cancelcloud.utils.Base64
import de.moltenKt.core.extension.forceCast
import de.moltenKt.core.extension.tryOrNull
import de.moltenKt.paper.extension.display.notification
import de.moltenKt.paper.extension.display.ui.buildPanel
import de.moltenKt.paper.extension.display.ui.item
import de.moltenKt.paper.extension.display.ui.set
import de.moltenKt.paper.extension.paper.getOfflinePlayer
import de.moltenKt.paper.extension.paper.getPlayer
import de.moltenKt.paper.extension.paper.server
import de.moltenKt.paper.extension.tasky.sync
import de.moltenKt.paper.structure.command.InterchangeUserRestriction
import de.moltenKt.paper.structure.command.StructuredInterchange
import de.moltenKt.paper.structure.command.completion.buildInterchangeStructure
import de.moltenKt.paper.structure.command.completion.component.CompletionAsset
import de.moltenKt.paper.tool.display.message.Transmission
import de.moltenKt.paper.tool.timing.tasky.TemporalAdvice.Companion
import de.moltenKt.unfold.extension.asStyledComponent
import de.moltenKt.unfold.text
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
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
            (server.onlinePlayers.map { it.name } + InventoryContent.databaseAllNames()).any {
                it.equals(
                    input,
                    ignoreCase
                )
            }
        },
        //transformiert die Eingabe zu einem Objekt (Spielername => Spieler)
        transformer = { input: String -> getPlayer(input) },
        //welche Eingabe soll die Liste anzeigen
        generator = { server.onlinePlayers.map { it.name } + InventoryContent.databaseAllNames() }
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
                    InventoryContent.databaseAllNames()
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

                    val target = tryOrNull {
                        InventoryContent.PlayerData(
                            getInput(1, completionAsset).uniqueId,
                            getInput(1, completionAsset).name,
                            Base64.itemStackArrayToBase64(getInput(1, completionAsset).inventory.contents.forceCast())!!
                        )
                    } ?: InventoryContent.getPlayerData(getInput(1))


                    val targetOfflinePlayer = target?.user?.let { getOfflinePlayer(it) }

                    buildPanel(6, false) {
                        this.label = "<green><gray>Inventory of ${target!!.name}".asStyledComponent
                        this.identity = "${target.user}"
                        this.icon = Material.CYAN_DYE.item {
                            this.label = Component.empty()
                        }
                        set(0..8, Material.GRAY_STAINED_GLASS_PANE.item {
                            blankLabel()
                        })

                        onOpen {
                            PimpedSync.coroutineScope.launch {

                                Base64.itemStackArrayFromBase64(target.inventory).forEachIndexed { index, itemStack ->
                                    if (itemStack != null) {
                                        //index bezieht sich auf das Pannel
                                        it.inventory[index + 9] = itemStack
                                    }
                                }
                            }
                        }

                        // If player is online:
                        onClick {
                            if ((targetOfflinePlayer != null) && (targetOfflinePlayer.isOnline)) {
                                val targetPlayer = targetOfflinePlayer.player!!

                                sync(Companion.delayed(.1.seconds)) {
                                    targetPlayer.inventory.contents =
                                        it.origin.clickedInventory?.contents?.drop(9)?.toTypedArray() ?: emptyArray()
                                }

                            }
                        }

                        // If player is offline
                        onClose {
                            if (targetOfflinePlayer?.isOnline == false) {
                                InventoryContent.databasePush(
                                    getOfflinePlayer(target.user),
                                    it.inventory.contents?.drop(9)?.toTypedArray().forceCast()
                                )
                            }
                        }


                    }.display(executor)

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