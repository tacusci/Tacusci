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
import mu.KLogging
import java.sql.Connection
import java.sql.SQLException
import java.util.*

/**
 * Created by tauraamui on 27/10/2016.
 */

abstract class DAO(var url: String, var dbProperties: Properties, var tableName: String, var connectionPool: ConnectionPool) : KLogging() {

    protected var connection: Connection? = null

    fun connect(): Boolean {
        try {
            open()
            logger.debug("Connected DAO to $tableName")
            return true
        } catch (e: SQLException) { logger.error(e.message) }
        return false
    }

    fun disconnect(): Boolean {
        try {
            close(connection!!)
            logger.debug("Disconnected DAO to $tableName")
            return true
        } catch (e: SQLException) { logger.error(e.message) }
        return false
    }

    @Throws(SQLException::class)
    private fun open() {
        try {
            connection = connectionPool.getConnection()
            connection?.autoCommit = false
        } catch (e: SQLException) {
            throw e
        }
    }

    @Throws(SQLException::class)
    private fun close(connection: Connection) {
        connectionPool.returnConnection(connection)
    }

    abstract fun count(): Int
}