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
 
 
 
package handlers

import db.daos.DAOManager
import db.daos.GroupDAO
import db.models.Group
import db.models.User
import db.models.isValid
import javax.print.attribute.standard.JobOriginatingUserName

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

    fun groupExists(groupName: String) {

    }

    fun isUserInGroup(userName: String, groupName: String) {}

    fun  addUserToGroup(username: String, groupName: String) {
        if (UserHandler.userExists(username)) {
            val groupDAO: GroupDAO = DAOManager.getDAO(DAOManager.TABLE.GROUPS) as GroupDAO
            if (groupDAO.groupExists(groupName)) {

            }
        }
    }
}