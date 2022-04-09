package de.cancelcloud

import de.cancelcloud.commands.InvseeInterChange
import de.cancelcloud.config.GeneralConfig
import de.cancelcloud.database.InventoryContent.createTables
import de.cancelcloud.listeners.JoinListener
import de.cancelcloud.listeners.QuitListener
import de.jet.jvm.tool.smart.identification.Identity
import de.jet.paper.extension.mainLog
import de.jet.paper.structure.app.App
import de.jet.paper.structure.app.AppCompanion
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class PimpedSync : App() {
    override val appCache = PimpedCache
    override val appIdentity = "PimpedSync"
    override val appLabel = "PimpedSync"
    override val companion = Companion

    override suspend fun preHello() {
        GeneralConfig.load()
        createTables()
    }

    override suspend fun hello() {

        add(JoinListener())
        add(QuitListener())
        add(InvseeInterChange())

        mainLog.info(ChatColor.GREEN.toString() + "Hello from PimpSync!")
    }

    override fun bye() {
        server.onlinePlayers.forEach{player ->
            player.kick(Component.text(ChatColor.RED.toString() + "Server restarting!"))
        }
        mainLog.info(ChatColor.RED.toString() + "Bye from PimpSync!")
    }

    companion object : AppCompanion<PimpedSync>() {
        override val predictedIdentity: Identity<PimpedSync> = Identity("PimpedSync")
    }

}