package db

import java.sql.Connection

/**
 * Created by alewis on 28/10/2016.
 */
class GenericDAO(connection: Connection, tableName: String) : DAO(connection, tableName) {

    override fun count(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
