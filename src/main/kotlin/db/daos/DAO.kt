package db.daos

import java.sql.Connection

/**
 * Created by tauraamui on 27/10/2016.
 */

abstract class DAO(connection: Connection, var tableName: String) {

    var connection: Connection? = connection

    abstract fun count(): Int
}