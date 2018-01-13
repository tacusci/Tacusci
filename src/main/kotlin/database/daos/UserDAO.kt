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
import database.models.User
import extensions.toBoolean
import extensions.toInt
import mu.KLogging
import utils.PasswordStorage
import java.sql.SQLException
import java.util.*
import java.util.regex.Pattern

/**
 * Created by tauraamui on 27/10/2016.
 */

class UserDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : GenericDAO(url, dbProperties, tableName, connectionPool) {

    companion object : KLogging()

    fun getUser(userID: Int): User {
        connect()
        val user = User(-1, -1, -1, "", "", "", "", false, true)
        try {
            val selectStatement = "SELECT * FROM $tableName WHERE ID_USERS=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, userID)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                user.id = resultSet.getInt("ID_USERS")
                user.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                user.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                user.fullName = resultSet.getString("FULL_NAME")
                user.username = resultSet.getString("USERNAME")
                user.password = resultSet.getString("AUTH_HASH")
                user.email = resultSet.getString("EMAIL")
                user.banned = resultSet.getBoolean("BANNED")
                user.rootAdmin = resultSet.getBoolean("ROOT_ADMIN")
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return user
    }

    fun getUser(username: String): User {
        return getUser(getUserID(username))
    }

    fun getUserID(username: String): Int {
        connect()
        var userID = -1
        try {
            //val selectStatement = "SELECT ID_USERS FROM $tableName WHERE BINARY USERNAME=?"

            val selectStatement = if (dbProperties.getProperty("server-type") == "POSTGRESQL") {
                "SELECT ID_USERS FROM $tableName WHERE USERNAME=?"
            } else {
                "SELECT ID_USERS FROM $tableName WHERE BINARY USERNAME=?"
            }

            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                userID = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return userID
    }

    fun insertUser(user: User): Boolean {
        connect()
        try {
            val createUserStatementString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, ROOT_ADMIN, USERNAME, AUTH_HASH, EMAIL, FULL_NAME, BANNED) VALUES (?,?,?,?,?,?,?,?) ${DAOManager.getConflictConstraintCommand("users_username_key")}"
            val preparedStatement = connection?.prepareStatement(createUserStatementString)
            //preparedStatement?.setString(1, count().toString())
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setBoolean(3, user.rootAdmin)
            preparedStatement?.setString(4, user.username)
            preparedStatement?.setString(5, PasswordStorage.createHash(user.password))
            preparedStatement?.setString(6, user.email)
            preparedStatement?.setString(7, user.fullName)
            preparedStatement?.setBoolean(8, user.banned)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun getUserAuthHash(username: String): String {
        connect()
        var authHash: String = ""
        try {
            val selectStatement = "SELECT AUTH_HASH FROM $tableName WHERE BINARY USERNAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                authHash = resultSet.getString("AUTH_HASH")
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        disconnect()
        return authHash
    }

    fun userExists(username: String): Boolean {
        connect()
        var count = 0
        try {

            val selectStatement= if (dbProperties.getProperty("server-type") == "POSTGRESQL") {
                "SELECT COUNT(*) FROM $tableName WHERE USERNAME=?"
            } else {
                "SELECT COUNT(*) FROM $tableName WHERE BINARY USERNAME=?"
            }

            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                count = resultSet.getInt(1)
            }
        } catch (e: SQLException) { e.printStackTrace(); disconnect() }
        disconnect()
        return count > 0
    }

    fun getUsernameFromEmail(email: String): String {
        connect()
        var username: String = ""
        try {
            val selectStatement = "SELECT USERNAME FROM $tableName WHERE EMAIL=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, email)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                username = resultSet.getString("USERNAME")
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        disconnect()
        return username
    }

    fun getUsernames(): MutableCollection<String> {
        connect()
        val usernameList = mutableListOf<String>()

        val selectStatement = "SELECT USERNAME FROM $tableName"
        val preparedStatement = connection?.prepareStatement(selectStatement)
        val resultSet = preparedStatement?.executeQuery()
        while (resultSet!!.next()) {
            usernameList.add(resultSet.getString("USERNAME"))
        }
        disconnect()
        return usernameList
    }

    fun getUsers(): MutableCollection<User> {
        connect()
        val userList = mutableListOf<User>()
        val selectStatement = "SELECT ID_USERS, CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, ROOT_ADMIN, USERNAME, AUTH_HASH, EMAIL, FULL_NAME, BANNED FROM $tableName"
        val preparedStatement = connection?.prepareStatement(selectStatement)
        val resultSet = preparedStatement?.executeQuery()
        while (resultSet!!.next()) {
            val user = User(-1, -1, -1, "", "", "", "", false, false)
            user.id = resultSet.getInt("ID_USERS")
            user.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
            user.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
            user.rootAdmin = resultSet.getBoolean("ROOT_ADMIN")
            user.username = resultSet.getString("USERNAME")
            user.password = resultSet.getString("AUTH_HASH")
            user.fullName = resultSet.getString("FULL_NAME")
            user.email = resultSet.getString("EMAIL")
            user.banned = resultSet.getBoolean("BANNED")
            userList.add(user)
        }
        disconnect()
        return userList
    }

    fun getRootAdmin(): User {
        connect()
        val user = User(-1, -1, -1, "", "", "", "", false, true)
        val selectStatement = "SELECT ID_USERS, CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, ROOT_ADMIN, USERNAME, EMAIL, FULL_NAME, BANNED FROM $tableName WHERE ROOT_ADMIN=?"
        val preparedStatement = connection?.prepareStatement(selectStatement)
        preparedStatement?.setBoolean(1, user.rootAdmin)
        val resultSet = preparedStatement?.executeQuery()
        if (resultSet!!.next()) {
            user.id = resultSet.getInt("ID_USERS")
            user.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
            user.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
            user.rootAdmin = resultSet.getBoolean("ROOT_ADMIN")
            user.username = resultSet.getString("USERNAME")
            user.email = resultSet.getString("EMAIL")
            user.fullName = resultSet.getString("FULL_NAME")
            user.banned = resultSet.getBoolean("BANNED")
        }
        disconnect()
        return user
    }

    fun updateRootAdmin(user: User): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET LAST_UPDATED_DATE_TIME=?, USERNAME=?, AUTH_HASH=? WHERE ROOT_ADMIN=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setString(2, user.username)
            val pattern = Pattern.compile("sha1\\:\\d*:\\d*:\\S*")
            val matcher = pattern.matcher(user.password)
            if (matcher.find())
                preparedStatement?.setString(3, user.password)
            else
                preparedStatement?.setString(3, PasswordStorage.createHash(user.password))
            preparedStatement?.setBoolean(4, user.rootAdmin)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun updateUser(user: User): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET LAST_UPDATED_DATE_TIME=?, USERNAME=?, AUTH_HASH=? WHERE BINARY USERNAME=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setString(2, user.username)
            preparedStatement?.setString(3, PasswordStorage.createHash(user.password))
            preparedStatement?.setString(4, user.username)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun deleteUser(user: User): Boolean {
        connect()
        //TODO("Must implement removing all other user references/mappings")
        try {
            val deleteStatement = "DELETE FROM $tableName WHERE ID_USERS=?"
            val preparedStatement = connection?.prepareStatement(deleteStatement)
            preparedStatement?.setInt(1, user.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun ban(username: String): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET BANNED=?, BANNED_DATE_TIME=? WHERE BINARY USERNAME=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setInt(1, 1)
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, username)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun unban(username: String): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET BANNED=?, BANNED_DATE_TIME=? WHERE BINARY USERNAME=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setInt(1, 0)
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, username)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun isBanned(username: String): Boolean {
        connect()
        var banned = false
        try {
            val selectStatement = "SELECT BANNED FROM $tableName WHERE BINARY USERNAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                banned = resultSet.getBoolean("BANNED")
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        disconnect()
        return banned
    }
}