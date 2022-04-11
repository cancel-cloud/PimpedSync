package de.cancelcloud

import de.jet.paper.structure.app.AppCache
import de.jet.paper.structure.app.cache.CacheDepthLevel
import de.jet.paper.structure.app.cache.CacheDepthLevel.KILL
import java.util.*

object PimpedCache : AppCache {

    override fun dropEntityData(entityIdentity: UUID, dropDepth: CacheDepthLevel) {

    }

    override fun dropEverything(dropDepth: CacheDepthLevel) {
        if (dropDepth.isDeeperThanOrEquals(KILL)) dataBasePlayers = emptyList()
    }

    var dataBasePlayers = emptyList<String>()

}