package de.cancelcloud.config

import de.cancelcloud.config.database.DataBaseConfig

@kotlinx.serialization.Serializable
data class ConfigFile(
    val database: DataBaseConfig = DataBaseConfig(),
)
