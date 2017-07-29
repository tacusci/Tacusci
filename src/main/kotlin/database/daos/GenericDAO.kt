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

import database.connections.ConnectionPool
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 * Created by alewis on 28/10/2016.
 */
open class GenericDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : DAO(url, dbProperties, tableName, connectionPool) {

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
