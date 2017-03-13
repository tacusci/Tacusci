package database.daos

import database.models.RouteEntity
import mu.KLogging
import java.sql.SQLException
import java.util.*

/**
 * Created by alewis on 10/02/2017.
 */
class RouteEntityDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

    fun saveOrUpdate(routeEntity: RouteEntity) {
    }

    fun insertRouteEntity(routeEntity: RouteEntity): Boolean {
        connect()
        try {
            val createRouteEntityStatement = "INSERT INTO $tableName (parentid, name)"
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }
}