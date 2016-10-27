package db

import java.net.URL
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Created by tauraamui on 27/10/2016.
 */

object DAOManager {

    var url = URL("")
    var username = ""
    var password = ""

    enum class TABLE {
        USERS
    }

    private var connection: Connection? = null

    fun init(url: URL, username: String, password: String) {
        Class.forName("com.mysql.jdbc.Driver")
        this.url = url
        this.username = username
        this.password = password
    }

    fun open() {
        try {
            if (connection == null || connection!!.isClosed) {
                connection = DriverManager.getConnection(url.path, username, password)
            }
        } catch (e: SQLException) { throw e }
    }

    fun close() {
        try {
            if (connection != null && !connection!!.isClosed) {
                connection!!.close()
            }
        } catch (e: SQLException) { throw e }
    }

    fun getDAO(table: TABLE) {
        when (table == TABLE.USERS) {

        }
    }
}