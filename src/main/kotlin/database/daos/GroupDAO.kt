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

import database.models.Group
import mu.KLogging
import java.sql.SQLException
import java.util.*

/**
 * Created by alewis on 20/12/2016.
 */
class GroupDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

    fun getGroupID(groupName: String): Int {
        connect()
        var groupID = -1
        try {
            val selectStatement = "SELECT ID_GROUPS FROM $tableName WHERE GROUP_NAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, groupName)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                groupID = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        disconnect()
        return groupID
    }

    fun insertGroup(group: Group): Boolean {
        connect()
        try {
            val createGroupStatementString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, GROUP_NAME, ID_PARENT_GROUP) VALUES (?,?,?,?)"
            val preparedStatement = connection?.prepareStatement(createGroupStatementString)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, group.name)
            preparedStatement?.setInt(4, group.parentGroupId)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun groupExists(groupName: String): Boolean {
        connect()
        var count = 0
        try {
            val selectStatement = "SELECT COUNT(*) FROM $tableName WHERE GROUP_NAME=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, groupName)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                count = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { e.printStackTrace(); disconnect() }
        disconnect()
        return count > 0
    }

    fun addUserToGroup(username: String, groupName: String): Boolean {
        connect()
        val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        val userID = userDAO.getUserID(username)
        val groupID = getGroupID(groupName)
        val user2groupDAO = DAOManager.getDAO(DAOManager.TABLE.USER2GROUP) as User2GroupDAO
        disconnect()
        return user2groupDAO.mapUserIDToGroupID(userID, groupID)
    }
}