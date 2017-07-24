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

    fun getGroup(groupId: Int): Group {
        connect()
        val group = Group()
        try {
            val selectStatement = "SELECT * FROM $tableName WHERE ID_GROUPS=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, groupId)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                group.id = resultSet.getInt("ID_GROUPS")
                group.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                group.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                group.name = resultSet.getString("GROUP_NAME")
                group.parentGroupId = resultSet.getInt("ID_PARENT_GROUP")
                group.defaultGroup = resultSet.getBoolean("DEFAULT_GROUP")
                group.hidden = resultSet.getBoolean("HIDDEN")
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return group }
        return group
    }

    fun getGroup(groupName: String): Group {
        return getGroup(getGroupID(groupName))
    }

    private fun getGroupID(groupName: String): Int {
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
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return groupID
    }

    fun getGroupID(groupName: String, closeConnection: Boolean = true): Int {
        if (!closeConnection) {
            return getGroupID(groupName)
        } else {
            val groupId = getGroupID(groupName)
            disconnect()
            return groupId
        }
    }

    fun insertGroup(group: Group): Boolean {
        connect()
        try {
            val createGroupStatementString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, GROUP_NAME, ID_PARENT_GROUP, DEFAULT_GROUP, HIDDEN) VALUES (?,?,?,?,?,?)"
            val preparedStatement = connection?.prepareStatement(createGroupStatementString)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, group.name)
            preparedStatement?.setInt(4, group.parentGroupId)
            preparedStatement?.setBoolean(5, group.defaultGroup)
            preparedStatement?.setBoolean(6, group.hidden)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun updateGroup(group: Group): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET LAST_UPDATED_DATE_TIME=?, GROUP_NAME=?, ID_PARENT_GROUP=?, DEFAULT_GROUP=?, HIDDEN=? WHERE ID_GROUPS=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setString(2, group.name)
            preparedStatement?.setInt(3, group.parentGroupId)
            preparedStatement?.setBoolean(4, group.defaultGroup)
            preparedStatement?.setBoolean(5, group.hidden)
            preparedStatement?.setInt(6, group.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun deleteGroup(group: Group): Boolean {
        connect()
        //TODO("Must implement removing all other group references/mappings")
        try {
            val deleteStatement = "DELETE FROM $tableName WHERE ID_GROUPS=?"
            val preparedStatement = connection?.prepareStatement(deleteStatement)
            preparedStatement?.setInt(1, group.id)
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

    fun getGroups(): MutableList<Group> {
        val groups = mutableListOf<Group>()
        connect()
        try {
            val selectStatment = "SELECT * FROM $tableName"
            val preparedStatement = connection?.prepareStatement(selectStatment)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                val group = Group()
                group.id = resultSet.getInt("ID_GROUPS")
                group.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                group.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                group.name = resultSet.getString("GROUP_NAME")
                group.parentGroupId = resultSet.getInt("ID_PARENT_GROUP")
                group.defaultGroup = resultSet.getBoolean("DEFAULT_GROUP")
                group.hidden = resultSet.getBoolean("HIDDEN")
                groups.add(group)
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return groups }
        disconnect()
        return groups
    }

    fun getGroupChildren(group: Group): MutableList<Group> {
        return getGroupChildren(group.id)
    }

    fun getGroupChildren(groupName: String): MutableList<Group> {
        return getGroupChildren(getGroupID(groupName))
    }

    fun getGroupChildren(groupId: Int): MutableList<Group> {
        val childGroups = mutableListOf<Group>()
        connect()
        try {
            val selectStatment = "SELECT * FROM $tableName WHERE ID_PARENT_GROUP=?"
            val preparedStatement = connection?.prepareStatement(selectStatment)
            preparedStatement?.setInt(1, groupId)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                val group = Group()
                group.id = resultSet.getInt("ID_GROUPS")
                group.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                group.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                group.name = resultSet.getString("GROUP_NAME")
                group.parentGroupId = resultSet.getInt("ID_PARENT_GROUP")
                group.defaultGroup = resultSet.getBoolean("DEFAULT_GROUP")
                group.hidden = resultSet.getBoolean("HIDDEN")
                childGroups.add(group)
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return childGroups }
        disconnect()
        return childGroups
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