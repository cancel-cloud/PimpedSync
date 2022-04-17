package de.cancelcloud.config

import de.cancelcloud.PimpedSync
import de.cancelcloud.config.database.DataBaseConfig
import de.moltenKt.core.extension.data.fromJson
import de.moltenKt.core.extension.data.toJson
import de.moltenKt.core.extension.div
import de.moltenKt.core.extension.tryOrNull
import de.moltenKt.paper.extension.mainLog
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.writeText

object GeneralConfig {

    /**
     * The Config is located in JETData not in plugins folder
     */
    private var configPath = PimpedSync.instance.appFolder / "config.json"

    private var _generalConfig: ConfigFile? = null

    var config: ConfigFile
        get() = _generalConfig ?: tryOrNull {
            configPath.toFile().readText().fromJson<ConfigFile>().also { mainLog.warning("data: '$it'") }
        }?.also { config = it } ?: ConfigFile().also { config = it }
        set(value) {
            configPath.apply {
                if (notExists()) {
                    parent.createDirectories()
                    createFile()
                }
            }
            configPath.writeText(value.toJson())
            _generalConfig = value
        }

    val databaseConfig: DataBaseConfig
        get() = config.database

    fun load() {
        config = config
        databaseConfig.connect()
    }

}