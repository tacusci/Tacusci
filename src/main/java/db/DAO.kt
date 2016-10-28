package db

import java.sql.Connection

/**
 * Created by tauraamui on 27/10/2016.
 */

abstract class DAO {

    var connection: Connection? = null
    var tableName: String = ""

    constructor(connection: Connection, tableName: String) {
        this.connection = connection
        this.tableName = tableName
    }

    abstract fun count(): Int
}