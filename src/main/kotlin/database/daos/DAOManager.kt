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
import database.SQLScript
import database.models.TacusciInfo
import extensions.toIntSafe
import mu.KLogging
import utils.Config
import utils.InternalResourceFile
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

/**
 * Created by tauraamui on 27/10/2016.
 */

object DAOManager : KLogging() {

    var url = ""
    var dbType = DB_TYPE.UNKNOWN
    var dbProperties = Properties()
    private var connectionPool = ConnectionPool()

    enum class DB_TYPE {
        UNKNOWN,
        MYSQL,
        POSTGRESQL
    }

    enum class TABLE {
        TACUSCI_INFO,
        USERS,
        USER2GROUP,
        GROUPS,
        ROUTE_ENTITIES,
        RESET_PASSWORD,
        PAGES,
        TEMPLATES,
        INCLUDES,
        ROUTE_PERMISSIONS,
        SQL_QUERIES
    }

    var connection: Connection? = null

    fun getDBType(): DB_TYPE {
        return dbType
    }

    fun isMySQL(): Boolean {
        return dbType == DB_TYPE.MYSQL
    }

    fun isPostgresql(): Boolean {
        return dbType == DB_TYPE.POSTGRESQL
    }

    fun isUnknown(): Boolean {
        return dbType == DB_TYPE.UNKNOWN
    }

    fun init(url: String, dbProperties: Properties) {
        this.url = url
        this.dbProperties = dbProperties
        registerJDBCDrivers()
        connectionPool = ConnectionPool(url, dbProperties)
    }

    fun registerJDBCDrivers(): Boolean {
        var successful = true
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance()
        } catch (e: ClassNotFoundException) {
            logger.error(e.message)
            successful = false
        }

        try {
            Class.forName("org.postgresql.Driver").newInstance()
        } catch (e: ClassNotFoundException) {
            logger.error(e.message)
            successful = false
        }
        return successful
    }

    fun workOutDBType() {
        when {
            url.startsWith("jdbc:mysql") -> {
                dbType = DB_TYPE.MYSQL
                dbProperties.setProperty("server-type", DB_TYPE.MYSQL.toString())
            }
            url.startsWith("jdbc:postgresql") -> {
                dbType = DB_TYPE.POSTGRESQL
                dbProperties.setProperty("server-type", DB_TYPE.POSTGRESQL.toString())
            }
            else -> {
                dbType = DB_TYPE.UNKNOWN
                dbProperties.setProperty("server-type", DB_TYPE.UNKNOWN.toString())
            }
        }
    }

    fun setup() {

        val sqlScriptData = when (dbType) {
            DB_TYPE.MYSQL -> {
                InternalResourceFile("/sql/mysql_setup_script.sql")
            }
            DB_TYPE.POSTGRESQL -> {
                InternalResourceFile("/sql/postgresql_setup_script.sql")
            }
            DB_TYPE.UNKNOWN -> {
                InternalResourceFile("")
            }
        }

        val sqlScript = SQLScript(sqlScriptData.inputStream)
        setup(sqlScript)
    }

    fun setup(internalScriptFile: InternalResourceFile) {
        val sqlScript = SQLScript(internalScriptFile.inputStream)
        setup(sqlScript)
    }

    fun setup(scriptFile: File) {
        val sqlScript = SQLScript(scriptFile.inputStream())
        val tacusciInfo = TacusciInfo(-1, Config.getProperty("tacusci-version-major").toIntSafe(), Config.getProperty("tacusci-version-minor").toIntSafe(), Config.getProperty("tacusci-version-revision").toIntSafe())
        setup(sqlScript, tacusciInfo)
    }

    fun setup(sqlScript: SQLScript, tacusciInfo: TacusciInfo = TacusciInfo()) {
        sqlScript.parse()

        if (tacusciInfo.versionNumberMajor >= 0 && tacusciInfo.versionNumberMinor >= 0 && tacusciInfo.versionNumberRevision >= 0) {
            sqlScript.replace("\$VERSION_MAJOR", " DEFAULT ${tacusciInfo.versionNumberMajor}")
            sqlScript.replace("\$VERSION_MINOR", " DEFAULT ${tacusciInfo.versionNumberMinor}")
            sqlScript.replace("\$VERSION_REVISION", " DEFAULT ${tacusciInfo.versionNumberRevision}")
        }

        sqlScript.replace("\$schema_name", Config.getProperty("schema-name"))
        sqlScript.executeStatements(connection!!)
    }

    fun executeScript(sqlScript: SQLScript, logScriptExecution: Boolean = false) {
        sqlScript.parse()
        sqlScript.replace("\$schema_name", Config.getProperty("schema-name"))
        sqlScript.executeStatements(connection!!, logScriptExecution)
    }

    fun connect() {
        try {
            DAOManager.open()
            logger.info("Connected to DB at ${url}")
        } catch (e: SQLException) {
            logger.error("Unable to connect to database at $url (Reason: ${e.message})... Terminating...")
            System.exit(-1)
        }
    }

    fun disconnect() {
        try {
            DAOManager.close()
            logger.info("Disconnected from database at ${url}")
        } catch (e: SQLException) {1
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

    //checks what SQL server type is in use and based on that returns command to add to end of inserts
    fun getConflictConstraintCommand(constraintName: String): String = if (dbProperties.getProperty("server-type") == "POSTGRESQL") "ON CONFLICT ON CONSTRAINT $constraintName DO NOTHING" else ""

    fun getDAO(table: TABLE): DAO {

        var schemaName = ""

        if (dbProperties.getProperty("server-type") == "POSTGRESQL")
            schemaName = Config.getProperty("schema-name") + "."

        return when (table) {
            TABLE.TACUSCI_INFO -> TacusciInfoDAO(url, dbProperties, "${schemaName}tacusci_info", connectionPool)
            TABLE.USERS -> UserDAO(url, dbProperties, "${schemaName}users", connectionPool)
            TABLE.GROUPS -> GroupDAO(url, dbProperties, "${schemaName}groups", connectionPool)
            TABLE.USER2GROUP -> User2GroupDAO(url, dbProperties, "${schemaName}user2group", connectionPool)
            TABLE.RESET_PASSWORD -> ResetPasswordDAO(url, dbProperties, "${schemaName}reset_password", connectionPool)
            TABLE.PAGES -> PageDAO(url, dbProperties, "${schemaName}pages", connectionPool)
            TABLE.TEMPLATES -> TemplateDAO(url, dbProperties, "${schemaName}templates", connectionPool)
            TABLE.INCLUDES -> IncludeDAO(url, dbProperties, "${schemaName}includes", connectionPool)
            TABLE.ROUTE_PERMISSIONS -> RoutePermissionDAO(url, dbProperties, "${schemaName}route_permissions", connectionPool)
            TABLE.SQL_QUERIES -> SQLQueriesDAO(url, dbProperties, "${schemaName}sql_queries", connectionPool)

            else -> {
                GenericDAO(url, dbProperties, "", connectionPool)
            }
        }
    }
}