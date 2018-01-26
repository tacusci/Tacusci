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
import database.models.SQLQuery
import mu.KLogging
import java.sql.SQLException
import java.util.*

class SQLQueriesDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : GenericDAO(url, dbProperties, tableName, connectionPool) {

    companion object : KLogging()

    fun getSQLQuery(queryID: Int): SQLQuery {
        connect()
        val sqlQuery = SQLQuery(-1, -1, -1, "", "", "")
        try {
            val selectStatement = "SELECT * FROM $tableName WHERE ID_QUERY=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, queryID)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                sqlQuery.id = resultSet.getInt("ID_QUERY")
                sqlQuery.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                sqlQuery.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                sqlQuery.label = resultSet.getString("QUERY_LABEL")
                sqlQuery.name = resultSet.getString("QUERY_NAME")
                sqlQuery.string = resultSet.getString("QUERY_STRING")
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return sqlQuery
    }

    fun getSQLQuery(queryName: String): SQLQuery {
        return getSQLQuery(getSQLQueryID(queryName))
    }

    fun getSQLQueryID(queryName: String): Int {
        connect()
        var sqlQueryId = -1
        try {
            val selectStatement = "SELECT ID_QUERY FROM $tableName WHERE ${if (DAOManager.isMySQL()) "BINARY " else ""}QUERY_NAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, queryName)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                sqlQueryId = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return sqlQueryId
    }

    fun insertSQLQuery(sqlQuery: SQLQuery): Boolean {
        connect()
        try {
            val createSQLQueryStatementString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, QUERY_LABEL, QUERY_NAME, QUERY_STRING) VALUES (?,?,?,?,?) ${DAOManager.getConflictConstraintCommand("sql_queries_query_name_key")}"
            val preparedStatement = connection?.prepareStatement(createSQLQueryStatementString)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, sqlQuery.label)
            preparedStatement?.setString(4, sqlQuery.name)
            preparedStatement?.setString(5, sqlQuery.string)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }
}