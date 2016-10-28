package db

import db.models.User
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

    fun createUser(user: User) {
        val createUserStatementString = "INSERT INTO $tableName (IDUSERS, USERNAME, SALT, HASH) VALUES (?,?,?,?)"
        val preparedStatement = connection?.prepareStatement(createUserStatementString)
        preparedStatement?.setString(1, count().toString())
        preparedStatement?.setString(2, user.username.toLowerCase())
        preparedStatement?.setString(3, )
    }

    fun getUsers() {

    }
}