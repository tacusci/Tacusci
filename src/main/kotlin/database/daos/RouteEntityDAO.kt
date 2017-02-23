package database.daos

import database.models.RouteEntity
import mu.KLogging
import java.util.*

/**
 * Created by alewis on 10/02/2017.
 */
class RouteEntityDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

    fun saveOrUpdate(routeEntity: RouteEntity) {

    }
}