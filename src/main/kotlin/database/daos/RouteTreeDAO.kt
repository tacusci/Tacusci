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