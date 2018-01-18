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
import database.models.TacusciInfo
import mu.KLogging
import java.sql.SQLException
import java.util.*

class TacusciInfoDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : GenericDAO(url, dbProperties, tableName, connectionPool) {

    companion object : KLogging()

    fun insertTacusciInfo(tacusciInfo: TacusciInfo): Boolean {
        connect()
        try {
            val insertStatement = "INSERT INTO $tableName (VERSION_NUMBER_MAJOR, VERSION_NUMBER_MINOR, VERSION_NUMBER_REVISION) VALUES (?,?,?)"
            val preparedStatement = connection?.prepareStatement(insertStatement)
            preparedStatement?.setInt(1, tacusciInfo.versionNumberMajor)
            preparedStatement?.setInt(2, tacusciInfo.versionNumberMinor)
            preparedStatement?.setInt(3, tacusciInfo.versionNumberRevision)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun getTacusciInfo(): TacusciInfo {
        connect()
        val tacusciInfo = TacusciInfo(-1, -1, -1, -1)
        try {
            val selectStatement = "SELECT * FROM $tableName"
            val resultSet = connection?.prepareStatement(selectStatement)?.executeQuery()
            if (resultSet!!.next()) {
                tacusciInfo.id = resultSet.getInt(1)
                tacusciInfo.versionNumberMajor = resultSet.getInt(2)
                tacusciInfo.versionNumberMinor = resultSet.getInt(3)
                tacusciInfo.versionNumberRevision = resultSet.getInt(4)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return tacusciInfo
    }

    fun updateTacusciInfo(tacusciInfo: TacusciInfo): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET VERSION_NUMBER_MAJOR=?, VERSION_NUMBER_MINOR=?, VERSION_NUMBER_REVISION=? WHERE ${if (DAOManager.isMySQL()) "BINARY " else ""}ID_TACUSCI_INFO=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setInt(1, tacusciInfo.versionNumberMajor)
            preparedStatement?.setInt(2, tacusciInfo.versionNumberMinor)
            preparedStatement?.setInt(3, tacusciInfo.versionNumberRevision)
            preparedStatement?.setInt(4, tacusciInfo.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }
}