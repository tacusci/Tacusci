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

import database.models.User
import mu.KLogging
import utils.PasswordStorage
import java.sql.SQLException
import java.util.*

/**
 * Created by tauraamui on 27/10/2016.
 */

class UserDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

    fun getUserID(username: String): Int {
        connect()
        var groupID = -1
        try {
            val selectStatement = "SELECT IDUSERS FROM $tableName WHERE USERNAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                groupID = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { GroupDAO.logger.error(e.message); disconnect() }
        return groupID
    }

    fun insertUser(user: User): Boolean {
        connect()
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
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun getUserAuthHash(username: String): String {
        connect()
        var authHash: String = ""
        try {
            val selectStatement = "SELECT AUTHHASH FROM $tableName WHERE USERNAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                authHash = resultSet.getString("AUTHHASH")
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        disconnect()
        return authHash
    }

    fun userExists(username: String): Boolean {
        connect()
        var count = 0
        try {
            val selectStatement = "SELECT COUNT(*) FROM $tableName WHERE USERNAME=?"
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
            val selectStatement = "SELECT USERNAME FROM ? WHERE EMAIL=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            preparedStatement?.setString(2, email)
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
        val selectStatement = "SELECT USERNAME, AUTHHASH, EMAIL, FULLNAME, BANNED FROM $tableName"
        val preparedStatement = connection?.prepareStatement(selectStatement)
        val resultSet = preparedStatement?.executeQuery()
        while (resultSet!!.next()) {
            val user = User("", "", "", "", 0)
            user.username = resultSet.getString("USERNAME")
            user.password = resultSet.getString("AUTHHASH")
            user.fullName = resultSet.getString("FULLNAME")
            user.email = resultSet.getString("EMAIL")
            user.banned = resultSet.getInt("BANNED")
            userList.add(user)
        }
        disconnect()
        return userList
    }

    fun ban(username: String): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET BANNED=? WHERE USERNAME=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setInt(1, 1)
            preparedStatement?.setString(2, username)
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
            val updateStatement = "UPDATE $tableName SET BANNED=? WHERE USERNAME=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setInt(1, 0)
            preparedStatement?.setString(2, username)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun isBanned(username: String): Int {
        connect()
        var banned = 0
        try {
            val selectStatement = "SELECT BANNED FROM $tableName WHERE USERNAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, username)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                banned = resultSet.getInt("BANNED")
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        disconnect()
        return banned
    }
}