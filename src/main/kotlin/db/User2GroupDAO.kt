package db

import mu.KLogging
import java.sql.Connection

/**
 * Created by alewis on 20/12/2016.
 */
class User2GroupDAO(connection: Connection, tableName: String) : GenericDAO(connection, tableName) {

    companion object : KLogging()
}