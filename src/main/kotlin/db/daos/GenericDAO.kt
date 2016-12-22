package db.daos

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Created by alewis on 28/10/2016.
 */
open class GenericDAO(connection: Connection, tableName: String) : DAO(connection, tableName) {

    @Throws(SQLException::class)
    override fun count(): Int {
        var count = 0
        val countStatementString = "SELECT COUNT(*) AS count FROM $tableName;"
        try {
            connection?.autoCommit = false
            val countStatement = connection?.prepareStatement(countStatementString)
            val resultSet: ResultSet = countStatement!!.executeQuery()
            while (resultSet.next()) {
                count = resultSet.getInt("count")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return count
    }
}
