package db

import com.sun.org.apache.xpath.internal.operations.Bool
import db.models.User
import mu.KLogging
import java.sql.Connection
import java.sql.SQLException
import java.sql.ResultSet

/**
 * Created by tauraamui on 27/10/2016.
 */

class UserDAO(connection: Connection, tableName: String) : GenericDAO(connection, tableName) {

    companion object : KLogging()

    fun insertUser(user: User): Boolean {
        try {
            val createUserStatementString = "INSERT INTO $tableName (username, authhash, email, fullname, banned) VALUES (?,?,?,?,?)"
            val preparedStatement = connection?.prepareStatement(createUserStatementString)
            //preparedStatement?.setString(1, count().toString())
            preparedStatement?.setString(1, user.username.toLowerCase())
            preparedStatement?.setString(2, PasswordStorage.createHash(user.password))
            preparedStatement?.setString(3, user.email)
            preparedStatement?.setString(4, user.fullName)
            preparedStatement?.setInt(5, user.banned)

            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            return true
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    fun getUserAuthHash(username: String): String {
        var authHash: String = ""
        try {
            val queryString = "SELECT AUTHHASH FROM $tableName WHERE USERNAME=?"
            val preparedStatement = connection?.prepareStatement(queryString)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                authHash = resultSet.getString("AUTHHASH")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return authHash
    }

    fun userExists(username: String): Boolean {
        var count = 0
        try {
            val selectStatement = "SELECT COUNT(*) FROM $tableName WHERE USERNAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                count = resultSet.getInt(1)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return count > 0
    }

    fun getUsernameFromEmail(email: String): String {
        var username: String = ""
        try {
            val selectStatement = "SELECT USERNAME FROM ? WHERE EMAIL=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            preparedStatement?.setString(2, email)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                username = resultSet.getString("USERNAME")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return username
    }

    fun getUsernames(): MutableCollection<String> {

        val usernameList = mutableListOf("")

        val selectStatement = "SELECT USERNAME FROM $tableName"
        val preparedStatement = connection?.prepareStatement(selectStatement)
        val resultSet = preparedStatement?.executeQuery()
        while (resultSet!!.next()) {
            usernameList.add(resultSet.getString("USERNAME"))
        }
        return usernameList
    }

    fun getUserBanned(username: String): Int {
        var banned = 0
        try {
            val selectStatement = "SELECT BANNED FROM $tableName WHERE USERNAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                banned = resultSet.getInt("BANNED")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return banned
    }

    fun getAdministrators(): MutableCollection<String> {
        return mutableListOf()
    }
}