package database.daos

import database.models.RouteElement
import mu.KLogging
import java.sql.SQLException
import java.util.*

/**
 * Created by alewis on 10/02/2017.
 */
class RouteElementDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

    fun getRouteElements(): MutableList<RouteElement> {
        connect()
        val routeElements = mutableListOf<RouteElement>()
        try {
            val selectStatement = "SELECT * FROM $tableName"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) { /* TODO: finish implementing */ }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return routeElements
    }
}