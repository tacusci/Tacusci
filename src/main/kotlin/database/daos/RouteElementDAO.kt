package database.daos

import mu.KLogging
import java.util.*

/**
 * Created by alewis on 10/02/2017.
 */
class RouteElementDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

}