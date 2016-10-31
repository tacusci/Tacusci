package db

import db.models.NewUser
import java.sql.Connection
import java.sql.SQLException
import java.sql.ResultSet

/**
 * Created by tauraamui on 27/10/2016.
 */

class UsersDAO : DAO {

    constructor(connection: Connection, tableName: String) : super(connection, tableName)

    @Throws(SQLException::class)
    override fun count(): Int {
        var count = 0
        val countStatementString = "SELECT COUNT(*) AS count FROM $tableName"
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

    fun insertUser(newUser: NewUser): Boolean {
        try {
            val createUserStatementString = "INSERT INTO $tableName (username, authhash, email, fullname) VALUES (?,?,?,?)"
            val preparedStatement = connection?.prepareStatement(createUserStatementString)
            //preparedStatement?.setString(1, count().toString())
            preparedStatement?.setString(1, newUser.username.toLowerCase())
            preparedStatement?.setString(2, PasswordStorage.createHash(newUser.password))
            preparedStatement?.setString(3, newUser.email)
            preparedStatement?.setString(4, newUser.fullName)
            preparedStatement?.execute()
            connection?.commit()
            return true
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    fun getUserAuthHash(username: String): String {
        var authHash: String = ""
        try {
            val queryString = "SELECT AUTHHASH FROM $tableName WHERE USERNAME='$username'"
            val statement = connection?.createStatement()
            val resultSet = statement?.executeQuery(queryString)
            while (resultSet!!.next()) {
                authHash = resultSet.getString("AUTHHASH")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return authHash
    }

    fun getUsernameFromEmail(email: String): String {
        var username: String = ""
        try {
            val queryString = "SELECT USERNAME FROM $tableName WHERE EMAIL='$email'"
            val statement = connection?.createStatement()
            val resultSet = statement?.executeQuery(queryString)
            while (resultSet!!.next()) {
                username = resultSet.getString("USERNAME")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return username
    }
}