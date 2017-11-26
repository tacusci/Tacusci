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



package app.core.handlers

import database.daos.DAOManager
import database.daos.GroupDAO
import database.daos.User2GroupDAO
import database.models.Group
import database.models.User
import mu.KLogging

/**
 * Created by alewis on 22/12/2016.
 */
object GroupHandler : KLogging() {

    val groupDAO: GroupDAO = DAOManager.getDAO(DAOManager.TABLE.GROUPS) as GroupDAO

    fun createGroup(group: Group): Boolean {
        groupDAO.getGroups()
        if (!group.isValid()) return false
        groupDAO.insertGroup(group)
        return true
    }

    fun addUserToGroup(user: User, groupName: String) {
        addUserToGroup(user.username, groupName)
    }

    fun addUserToGroup(username: String, groupName: String) {
        if (UserHandler.userExists(username)) {
            if (groupExists(groupName)) {
                groupDAO.addUserToGroup(username, groupName)
            } else {
                logger.error("The group $groupName doesn't exist")
            }
        } else {
            logger.error("The user $username doesn't exist")
        }
    }

    fun removeUserFromGroup(user: User, groupName: String) {
        removeUserFromGroup(user.username, groupName)
    }

    fun removeUserFromGroup(username: String, groupName: String) {
        if (UserHandler.userExists(username)) {
            if (groupExists(groupName)) {
                if (GroupHandler.groupDAO.getGroup(groupName).defaultGroup && UserHandler.userDAO.getUser(username).rootAdmin) return
                val user2groupDAO = DAOManager.getDAO(DAOManager.TABLE.USER2GROUP) as User2GroupDAO
                user2groupDAO.removeUserAndGroupMap(UserHandler.userDAO.getUserID(username), groupDAO.getGroupID(groupName))
            }
        }
    }

    fun groupExists(groupName: String): Boolean {
        return groupDAO.groupExists(groupName)
    }

    fun getUsersInGroup(groupName: String): List<User> = UserHandler.getUsers().filter { user -> userInGroup(user, groupName) }

    fun userInGroup(user: User, groupName: String): Boolean {
        return userInGroup(user.username, groupName)
    }

    fun userInGroup(username: String, groupName: String): Boolean {
        val user2GroupDAO = DAOManager.getDAO(DAOManager.TABLE.USER2GROUP) as User2GroupDAO
        if (user2GroupDAO.areUserAndGroupMapped(UserHandler.userDAO.getUserID(username), GroupHandler.groupDAO.getGroupID(groupName))) return true

        groupDAO.getGroupChildren(groupName).forEach { childGroup ->
            if (userInGroup(username, childGroup.name))
                return true
            else
                groupDAO.getGroupChildren(childGroup).forEach { userInGroup(username, it.name) }
        }
        return false
    }

    fun deleteGroup(groupName: String): Boolean {
        return deleteGroup(groupDAO.getGroup(groupName))
    }

    fun deleteGroup(group: Group): Boolean {
        if (groupExists(group.name)) {
            val user2GroupDAO = DAOManager.getDAO(DAOManager.TABLE.USER2GROUP) as User2GroupDAO
            user2GroupDAO.removeAllGroupsMaps(group.id)
            return groupDAO.deleteGroup(group)
        }
        return false
    }

    /*
    fun userInGroupRec(username: String, groupName: String): Boolean {
        if (userInGroup(username, groupName)) return true
        groupDAO.getGroupChildren(groupName).forEach { childGroup ->
            if (userInGroup(username, childGroup.name))
                return true
            else
                groupDAO.getGroupChildren(childGroup).forEach { userInGroupRec(username, it.name) }
        }
        return false
    }
    */

    fun displayGroupsAndChildren(groupName: String) {
        println("Group: " + groupName)
        println("Parent: ${groupDAO.getGroup(groupDAO.getGroup(groupName).parentGroupId).name}")
        groupDAO.getGroupChildren(groupName).forEach { childGroup ->
            println("Child of $groupName: ${childGroup.name}")
            groupDAO.getGroupChildren(childGroup).forEach { displayGroupsAndChildren(it.name) }
        }
    }
}