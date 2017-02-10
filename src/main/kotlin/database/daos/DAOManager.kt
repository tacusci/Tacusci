/*
# DON'T BE A DICK PUBLIC LICENSE

> Version 1.1, December 2016

> Copyright (C) 2016 Adam Prakash Lewis
 
 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document.

> DON'T BE A DICK PUBLIC LICENSE
> TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

 1. Do whatever you like with the original work, just don't be a dick.

     Being a dick includes - but is not limited to - the following instances:

	 1a. Outright copyright infringement - Don't just copy this and change the name.  
	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.  
	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.  

 2. If you become rich through modifications, related works/services, or supporting the original work,
 share the love. Only a dick would make loads off this work and not buy the original work's 
 creator(s) a pint.
 
 3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes 
 you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */
 
 
 
 package database.daos

import database.SQLScript
import mu.KLogging
import utils.InternalResourceFile
import java.io.File
import java.io.FileInputStream
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by tauraamui on 27/10/2016.
 */

object DAOManager : KLogging() {

    var url = ""
    var dbProperties = Properties()

    var randomPollThread = Thread()
    var randomPollThreadRunning = false

    enum class TABLE {
        USERS,
        USER2GROUP,
        GROUPS,
        ROUTE_ELEMENTS
    }

    public var connection: Connection? = null

    fun init(url: String, dbProperties: Properties) {
        this.url = url
        this.dbProperties = dbProperties
        logger.info("Set up database settings to connect to $url")
    }

    fun setup() {
        val sqlScriptData = InternalResourceFile("/sql/sql_setup_script.sql")
        val sqlScript = SQLScript(sqlScriptData.inputStream)
        sqlScript.parse()
        sqlScript.executeStatements(connection!!)
    }

    fun setup(internalScriptFile: InternalResourceFile) {
        val sqlScript = SQLScript(internalScriptFile.inputStream)
        sqlScript.parse()
        sqlScript.executeStatements(connection!!)
    }

    fun setup(scriptFile: File) {
        val sqlScript = SQLScript(scriptFile.inputStream())
        sqlScript.parse()
        sqlScript.executeStatements(connection!!)
    }

    fun connect() {
        try {
            DAOManager.open()
            logger.info("Connected to DB at ${url}")
        } catch (e: SQLException) {
            logger.error("Unable to connect to database at ${url}... Terminating...")
            System.exit(-1)
        }
    }

    fun disconnect() {
        try {
            DAOManager.close()
            logger.info("Disconnected from database at ${url}")
        } catch (e: SQLException) {
            logger.error(e.message)
        }
    }

    @Throws(SQLException::class)
    private fun open(): Connection {
        try {
            if (connection == null || connection!!.isClosed) {
                connection = DriverManager.getConnection(url, dbProperties)
                connection?.autoCommit = false
            }
            return connection!!
        } catch (e: SQLException) {
            throw e
        }
    }

    @Throws(SQLException::class)
    private fun close() {
        try {
            if (connection != null && !connection!!.isClosed) {
                connection!!.close()
            }
        } catch (e: SQLException) {
            throw e
        }
    }

    fun getDAO(table: TABLE): DAO {
        when (table) {
            TABLE.USERS -> return UserDAO(url, dbProperties, "users")
            TABLE.USER2GROUP -> return User2GroupDAO(url, dbProperties, "user2group")
            TABLE.GROUPS -> return GroupDAO(url, dbProperties, "groups")
            TABLE.ROUTE_ELEMENTS -> return RouteElementDAO(url, dbProperties, "routeelements")
            else -> {
                return GenericDAO(url, dbProperties, "")
            }
        }
    }
}