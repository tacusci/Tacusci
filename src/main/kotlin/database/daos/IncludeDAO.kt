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
import database.models.Include
import mu.KLogging
import java.sql.SQLException
import java.util.*

class IncludeDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : GenericDAO(url, dbProperties, tableName, connectionPool) {

    companion object : KLogging()

    fun insertInclude(include: Include): Boolean {
        connect()
        return try {
            val createIncludeString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, INCLUDE_TITLE, INCLUDE_CONTENT, AUTHOR_USER_ID) VALUES (?,?,?,?,?)"
            val preparedStatement = connection?.prepareStatement(createIncludeString)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, include.title)
            preparedStatement?.setString(4, include.content.trim().removeSuffix("\r\n"))
            preparedStatement?.setInt(5, include.authorUserId)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); false }
    }

    fun updateInclude(include: Include): Boolean {
        return try {
            val updateStatement = "UPDATE $tableName SET LAST_UPDATED_DATE_TIME=?, INCLUDE_TITLE=?, INCLUDE_CONTENT=?, AUTHOR_USER_ID=? WHERE ID_INCLUDE=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setString(2, include.title)
            preparedStatement?.setString(3, include.content)
            preparedStatement?.setInt(4, include.authorUserId)
            preparedStatement?.setInt(5, include.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); false }
    }

    fun deleteInclude(include: Include): Boolean {
        connect()
        return try {
            val deleteStatement = "DELETE FROM $tableName WHERE ID_INCLUDE?"
            val preparedStatement = connection?.prepareStatement(deleteStatement)
            preparedStatement?.setInt(1, include.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); false }
    }

    fun getIncludeIdByTitle(includeTitle: String): Int {
        connect()
        var includeId = -1
        try {
            val selectStatement = "SELECT ID_INCLUDE FROM $tableName WHERE INCLUDE_TITLE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, includeTitle)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                includeId = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return includeId
    }

    fun getIncludeById(includeId: Int): Include {
        val include = Include()
        connect()
        return try {
            val selectStatement = "SELECT * FROM $tableName WHERE ID_INCLUDE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, includeId)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                include.id = resultSet.getInt("ID_INCLUDE")
                include.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                include.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                include.title = resultSet.getString("INCLUDE_TITLE")
                include.content = resultSet.getString("INCLUDE_CONTENT")
                include.authorUserId = resultSet.getInt("AUTHOR_USER_ID")
            }
            include
        } catch (e: SQLException) { logger.error(e.message); disconnect(); include }
    }

    fun getIncludeById(includeId: Int, closeConnection: Boolean): Include {
        return if (!closeConnection) {
            getIncludeById(includeId)
        } else {
            val include = getIncludeById(includeId)
            disconnect()
            include
        }
    }

    fun getAllIncludes(): MutableList<Include> {
        val includes = mutableListOf<Include>()
        connect()
        try {
            val selectStatement = "SELECT ID_INCLUDE FROM $tableName"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                val includeId = resultSet.getInt("ID_INCLUDE")
                includes.add(getIncludeById(includeId))
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return includes
    }
}