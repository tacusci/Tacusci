package db

import extensions.readTextAndClose
import mu.KLogging
import java.io.File
import java.net.URL
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

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

    fun setup(schemaName: String) {
        val resultSet = connection?.metaData?.catalogs
        var tvfSchemaExists = false

        while (resultSet!!.next()) {
            val databaseName = resultSet.getString(1)
            if (schemaName == databaseName) { tvfSchemaExists = true; break }
        }

        if (!tvfSchemaExists) {
            logger.info { "Database schema $schemaName@$url doesn't exist" }
            val sqlFile = File("tvf_database_setup.sql")
            val sqlFileString = sqlFile.readText()
            logger.info { "Found schema creation file tvf_database_setup.sql" }
            val sqlStatements = sqlFileString.split(";")
            sqlStatements.filter { it.isNotBlank() && it.isNotEmpty() }.forEach { statement ->
                val preparedStatement = connection?.prepareStatement("$statement;")
                logger.info { "Executing statement: $statement;" }
                preparedStatement?.execute()
                preparedStatement?.closeOnCompletion()
            }
        }
        resultSet.close()
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
}