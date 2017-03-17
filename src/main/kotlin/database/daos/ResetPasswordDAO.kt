package database.daos

import mu.KLogging
import java.sql.SQLException
import java.util.*

/**
 * Created by tauraamui on 13/03/2017.
 */
class ResetPasswordDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

     fun insertAuthHash(userId: Int, authHash: String) {
        connect()
        try {
            val insertAuthHashStatement = "INSERT INTO $tableName (idusers, authhash) VALUES (?,?)"
            val preparedStatement = connection?.prepareStatement(insertAuthHashStatement)
            preparedStatement?.setInt(1, userId)
            preparedStatement?.setString(2, authHash)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
    }

     fun updateAuthHash(userId: Int, authHash: String) {
        connect()
        try {
            val updateAuthHashStatement = "UPDATE $tableName SET AUTHHASH=? WHERE IDUSERS=?"
            val preparedStatement = connection?.prepareStatement(updateAuthHashStatement)
            preparedStatement?.setString(1, authHash)
            preparedStatement?.setInt(2, userId)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
        }  catch (e: SQLException) { logger.error(e.message); disconnect() }
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

    fun getAuthHash(userId: Int): String {
        connect()
        var authHash = ""
        try {
            val selectStatement = "SELECT AUTHHASH FROM $tableName WHERE IDUSERS=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, userId)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                authHash = resultSet.getString("AUTHHASH")
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return authHash
    }
}