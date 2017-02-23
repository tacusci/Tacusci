package database.daos

import database.models.RouteEntityTree
import mu.KLogging

/**
 * Created by alewis on 23/02/2017.
 */

class RouteTreeDAO {

    companion object : KLogging()

    var routeEntityDAO = DAOManager.getDAO(DAOManager.TABLE.ROUTE_ELEMENTS) as RouteEntityDAO

    fun saveOrUpdate(routeEntityTree: RouteEntityTree) {
        val routeEntity = routeEntityTree.toList()
        routeEntity.reverse()
        routeEntity.forEach { routeEntityNode ->
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