package db

import com.sun.org.apache.xpath.internal.operations.Bool
import extensions.readTextAndClose
import mu.KLogging
import utils.Config
import java.io.File
import java.net.URL
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
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
        USERS
    }

    private var connection: Connection? = null

    fun init(url: String, username: String, password: String) {
        Class.forName("com.mysql.jdbc.Driver")
        this.url = url
        this.username = username
        this.password = password
    }

    fun setup() {
        val databaseSetupFile = DatabaseSetupFile(File(Config.getProperty("sql_setup_script_location")))
        databaseSetupFile.pass()

        databaseSetupFile.schemas.forEach { name, line ->
            if (!schemaExists(name)) {
                val statement = connection?.prepareStatement(line)
                try {
                    statement?.execute()
                    statement?.closeOnCompletion()
                } catch (e: SQLException) {
                    logger.error("SQL Exception: ${e.message}")
                }
            }
        }

        /*
        val resultSet = connection?.metaData?.catalogs
        var tvfSchemaExists = false

        while (resultSet!!.next()) {
            val databaseName = resultSet.getString(1)
            if (schemaName == databaseName) { tvfSchemaExists = true; break }
        }

        if (!tvfSchemaExists) {
            logger.info("Database schema $schemaName doesn't exist")
            val sqlFile = File(Config.getProperty("sql_setup_script_location"))
            val sqlFileString = sqlFile.readText()
            logger.info("Found schema creation file ${sqlFile.name}")
            val sqlStatements = sqlFileString.split(";")
            sqlStatements.filter { it.isNotBlank() && it.isNotEmpty() }.forEach { statement ->
                val preparedStatement = connection?.prepareStatement("$statement;")
                logger.info("Executing statement: ${statement.replace("\n", "")};")
                preparedStatement?.execute()
                preparedStatement?.closeOnCompletion()
            }
        }
        resultSet.close()
        */
    }

    fun connect() {
        try {
            DAOManager.open()
            logger.info("Connected to DB at ${DAOManager.url}")
        } catch (e: SQLException) {
            logger.error("Unable to connect to db at ${DAOManager.url}... Terminating...")
            System.exit(-1)
        }
    }

    @Throws(SQLException::class)
    fun open() {
        try {
            if (connection == null || connection!!.isClosed) {
                connection = DriverManager.getConnection(url, username, password)
                connection?.autoCommit = false
            }
        } catch (e: SQLException) {
            throw e
        }
    }

    fun close() {
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
            else -> {
                return GenericDAO(connection!!, "")
            }
        }
    }

    fun schemaExists(schemaName: String): Boolean {

        val resultSet = connection?.metaData?.catalogs

        var schemaExists = false

        while (resultSet!!.next()) {
            println(resultSet.getString(1))
            val existingSchemaName = resultSet.getString(1)
            if (schemaName == existingSchemaName) schemaExists = true; break
        }
        return schemaExists
    }
}