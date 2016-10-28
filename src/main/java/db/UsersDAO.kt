package db

import java.sql.Connection
import java.sql.SQLException

/**
 * Created by tauraamui on 27/10/2016.
 */

class UsersDAO : GenericDAO() {

    @Throws(SQLException::class)
    override fun count(): Int {
        val query: String = "SELECT COUNT(*) AS count FROM "+this.tableName;
        return 0
    }
}