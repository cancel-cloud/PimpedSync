package de.cancelcloud

import de.jet.paper.structure.app.AppCache
import de.jet.paper.structure.app.cache.CacheDepthLevel
import java.util.*

class PimpedCache : AppCache {
    override fun dropEntityData(entityIdentity: UUID, dropDepth: CacheDepthLevel) {

    }

    override fun dropEverything(dropDepth: CacheDepthLevel) {

    }
}