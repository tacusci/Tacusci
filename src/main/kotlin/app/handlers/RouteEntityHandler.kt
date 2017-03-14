package app.handlers

import database.daos.DAOManager
import mu.KLogging

/**
 * Created by alewis on 10/02/2017.
 */

object RouteEntityHandler : KLogging() {

    enum class ROUTE_ENTITY_TYPE {
        PATH,
        PAGE
    }

    val routeElementDAO = DAOManager.getDAO(DAOManager.TABLE.ROUTE_ENTITIES)
}