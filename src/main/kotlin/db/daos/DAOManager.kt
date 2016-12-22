package db.daos

import com.sun.org.apache.xpath.internal.operations.Bool
import db.SQLScript
import extensions.readTextAndClose
import mu.KLogging
import utils.Config
import java.io.File
import java.net.URL
import java.sql.*
import javax.xml.crypto.Data
import javax.xml.validation.Schema

/**
 * Created by tauraamui on 27/10/2016.
 */

object DAOManager : KLogging() {

    var url = ""
    var username = ""
    var password = ""

    enum class TABLE {
        USERS,
        USER2GROUP,
        GROUPS
    }

    private var connection: Connection? = null

    fun init(url: String, username: String, password: String) {
        this.url = url
        this.username = username
        this.password = password
    }

    fun setup() {
        val sqlScript = SQLScript(File(Config.props.getProperty("sql_setup_script_location")))
        sqlScript.parse()
        sqlScript.executeStatements(connection!!)
    }

    fun connect() {
        try {
            DAOManager.open()
            logger.info("Connected to DB at ${url}")
        } catch (e: SQLException) {
            logger.error("Unable to connect to db at ${url}... Terminating...")
            System.exit(-1)
        }
    }

    fun disconnect() {
        try {
            DAOManager.close()
            logger.info("Disconnected from db at ${url}")
        } catch (e: SQLException) {
            logger.error(e.message)
        }
    }

    @Throws(SQLException::class)
    private fun open() {
        try {
            if (connection == null || connection!!.isClosed) {
                connection = DriverManager.getConnection(url, username, password)
                connection?.autoCommit = false
            }
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
            TABLE.USERS -> return UserDAO(connection!!, "users")
            TABLE.USER2GROUP -> return User2GroupDAO(connection!!, "user2group")
            TABLE.GROUPS -> return GroupDAO(connection!!, "groups")
            else -> {
                return GenericDAO(connection!!, "")
            }
        }
    }
}