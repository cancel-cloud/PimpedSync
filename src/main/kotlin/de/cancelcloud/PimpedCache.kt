package de.cancelcloud

import de.jet.paper.structure.app.AppCache
import de.jet.paper.structure.app.cache.CacheDepthLevel
import java.util.*

object PimpedCache : AppCache {
    override fun dropEntityData(entityIdentity: UUID, dropDepth: CacheDepthLevel) {

    }

    override fun dropEverything(dropDepth: CacheDepthLevel) {

    }

    var dataBasePlayers = emptyList<String>()
}