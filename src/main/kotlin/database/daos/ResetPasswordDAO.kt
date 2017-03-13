package database.daos

import mu.KLogging
import java.sql.SQLException
import java.util.*

/**
 * Created by tauraamui on 13/03/2017.
 */
class ResetPasswordDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

    fun createOrUpdateAuthHash(userId: Int) {
        connect()
        try {
            var actionString = ""
            if (authHashExists(userId)) actionString = "INSERT" else actionString = "UPDATE"
            val statement = "$actionString $tableName SET IDUSERS=?"
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
    }

    fun authHashExists(userId: Int): Boolean {
        connect()
        var count = 0
        try {
            val selectStatement = "SELECT COUNT(*) FROM $tableName WHERE IDUSERS=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, userId)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                count = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        disconnect()
        return count > 0
    }
}