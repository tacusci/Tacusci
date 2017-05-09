/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016-2017
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

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
            val insertAuthHashStatement = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, ID_USERS, AUTH_HASH, EXPIRED) VALUES (?,?,?,?,?)"
            val preparedStatement = connection?.prepareStatement(insertAuthHashStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setInt(3, userId)
            preparedStatement?.setString(4, authHash)
            preparedStatement?.setInt(5, 0)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
    }

     fun updateAuthHash(userId: Int, authHash: String, expired: Int) {
        connect()
        try {
            val updateAuthHashStatement = "UPDATE $tableName SET LAST_UPDATED_DATE_TIME=?, AUTH_HASH=?, EXPIRED=? WHERE ID_USERS=?"
            val preparedStatement = connection?.prepareStatement(updateAuthHashStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setString(2, authHash)
            preparedStatement?.setInt(3, expired)
            preparedStatement?.setInt(4, userId)
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
            val selectStatement = "SELECT COUNT(*) FROM $tableName WHERE ID_USERS=?"
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

    fun authHashExpired(authHash: String): Boolean {
        var expired = 0
        connect()
        try {
            val selectStatement = "SELECT EXPIRED FROM $tableName WHERE AUTH_HASH=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, authHash)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                expired = resultSet.getInt("EXPIRED")
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return expired > 0
    }

    fun getAuthHash(userId: Int): String {
        connect()
        var authHash = ""
        try {
            val selectStatement = "SELECT AUTH_HASH FROM $tableName WHERE ID_USERS=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, userId)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                authHash = resultSet.getString("AUTH_HASH")
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return authHash
    }

    fun getLastUpdatedDateTime(authHash: String): Long {
        connect()
        var lastUpdatedDateTime = -1L
        try {
            val selectStatement = "SELECT LAST_UPDATED_DATE_TIME FROM $tableName WHERE AUTH_HASH=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, authHash)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return lastUpdatedDateTime
    }
}