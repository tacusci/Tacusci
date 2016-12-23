package db.handlers

import db.daos.DAOManager
import db.daos.GroupDAO
import db.models.Group
import db.models.isValid

/**
 * Created by alewis on 22/12/2016.
 */
object GroupHandler {

    fun createGroup(group: Group): Boolean {
        if (!group.isValid()) return false
        val groupDAO: GroupDAO = DAOManager.getDAO(DAOManager.TABLE.GROUPS) as GroupDAO
        groupDAO.insertGroup(group)
        return true
    }
}