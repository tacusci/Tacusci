/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
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

import database.ConnectionPool
import mu.KLogging
import java.sql.SQLException
import java.util.*

/**
 * Created by alewis on 20/12/2016.
 */
class User2GroupDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : GenericDAO(url, dbProperties, tableName, connectionPool) {

    companion object : KLogging()

    fun mapUserIDToGroupID(userID: Int, groupID: Int): Boolean {
        if (!areUserAndGroupMapped(userID, groupID)) {
            connect()
            try {
                val insertUserIntoGroupStatement = "INSERT INTO $tableName (ID_USERS, LAST_UPDATED_DATE_TIME, ID_GROUPS) VALUES (?,?,?)"
                val preparedStatement = connection?.prepareStatement(insertUserIntoGroupStatement)
                preparedStatement?.setInt(1, userID)
                preparedStatement?.setLong(2, System.currentTimeMillis())
                preparedStatement?.setInt(3, groupID)
                preparedStatement?.execute()
                connection?.commit()
                preparedStatement?.close()
                disconnect()
                return true
            } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
        }
        disconnect()
        return false
    }

    fun areUserAndGroupMapped(userID: Int, groupID: Int): Boolean {
        connect()
        var count = 0
        try {
            val selectStatement = "SELECT COUNT(*) FROM $tableName WHERE ID_USERS=? AND ID_GROUPS=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, userID)
            preparedStatement?.setInt(2, groupID)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                count = resultSet.getInt(1)
            }
            preparedStatement.close()
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
        return count > 0
    }

    fun removeUserAndGroupMap(userID: Int, groupID: Int): Boolean {
        if (areUserAndGroupMapped(userID, groupID)) {
            connect()
            try {
                val removeUserFromGroupStatement = "DELETE FROM $tableName WHERE ID_USERS=? AND ID_GROUPS=?"
                val preparedStatement = connection?.prepareStatement(removeUserFromGroupStatement)
                preparedStatement?.setInt(1, userID)
                preparedStatement?.setInt(2, groupID)
                preparedStatement?.execute()
                connection?.commit()
                preparedStatement?.close()
                disconnect()
                return true
            } catch (e: SQLException) { logger.error(e.message); disconnect(); }
        }
        return false
    }

    fun removeAllGroupsMaps(groupID: Int): Boolean {
        connect()
        try {
            val deleteStatement = "DELETE FROM $tableName WHERE ID_GROUPS=?"
            val preparedStatement = connection?.prepareStatement(deleteStatement)
            preparedStatement?.setInt(1, groupID)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }
}