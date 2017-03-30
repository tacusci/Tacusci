/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

package database.daos

import database.models.RouteEntityTree
import mu.KLogging

/**
 * Created by alewis on 23/02/2017.
 */

class RouteTreeDAO {

    companion object : KLogging()

    var routeEntityDAO = DAOManager.getDAO(DAOManager.TABLE.ROUTE_ENTITIES) as RouteEntityDAO

    fun saveOrUpdate(routeEntityTree: RouteEntityTree) {
        val routeEntities = routeEntityTree.toList()
        routeEntities.reverse()
        routeEntities.forEach { routeEntityNode ->
            val routeEntity = routeEntityNode.data
            routeEntityDAO.saveOrUpdate(routeEntity)
            val parentId = routeEntity.id
            routeEntityNode.children.forEach { childEntityNode ->
                val childRouteEntity = childEntityNode.data
                childRouteEntity.parentId = parentId
                routeEntityDAO.saveOrUpdate(childRouteEntity)
            }
        }
    }
}