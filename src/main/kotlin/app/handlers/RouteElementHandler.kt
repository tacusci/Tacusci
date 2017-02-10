package app.handlers

import database.daos.DAOManager
import database.daos.RouteElementDAO
import mu.KLogging

/**
 * Created by alewis on 10/02/2017.
 */

object RouteElementHandler : KLogging() {

    enum class ROUTE_ELEMENT {
        PATH,
        PAGE
    }

    val routeElementDAO = DAOManager.getDAO(DAOManager.TABLE.ROUTE_ELEMENTS)
}