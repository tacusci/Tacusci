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

import database.daos.DAOManager
import database.daos.UserDAO
import database.models.User
import database.models.isValid
import mu.KLogging
import spark.Session
import utils.Config
import utils.PasswordStorage

/**
 * Created by tauraamui on 24/10/2016.
 */

object  UserHandler : KLogging() {

    val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
    var defaultUser = User(Config.getProperty("default_admin_user"), Config.getProperty("default_admin_user"), Config.getProperty("default_admin_password"), Config.getProperty("default_admin_email"), 0)

    fun login(session: Session, username: String, password: String): Boolean {
        logger.info("Attempting to login $username")

        val authHash = userDAO.getUserAuthHash(username)

        if (authHash.isNotBlank() && authHash.isNotEmpty()) {
            if (PasswordStorage.verifyPassword(password, authHash)) {
                session.attribute("logged_in", true)
                session.attribute("username", username)
                session.attribute("login_error", false)
                logger.info("Login successful")
            } else {
                session.attribute("login_error", true)
                logger.info("Login unsuccessful, incorrect password...")
                return false
            }
        } else {
            session.attribute("login_error", true)
            logger.info("Login unsuccessful, username not recognised")
            return false
        }
        return true
    }

    fun logout(session: Session) {
        session.removeAttribute("logged_in")
        session.removeAttribute("username")
        session.attributes().clear()
    }

    fun isLoggedIn(session: Session): Boolean {
        if (session.attributes().isNotEmpty()) {
            if (session.attributes().contains("logged_in")) {
                val loggedIn: Boolean = session.attribute("logged_in")
                return loggedIn
            }
        }
        return false
    }

    fun getLoggedInUsername(session: Session): String {
        if (isLoggedIn(session)) {
            if (session.attributes().contains("username")) {
                return session.attribute("username")
            }
        }
        return ""
    }

    fun createDefaultUser(): Boolean {
        return createUser(defaultUser)
    }

    fun createUser(user: User): Boolean {
        if (!user.isValid()) return false
        userDAO.insertUser(user)
        GroupHandler.addUserToGroup(user, "members")
        return true
    }

    fun userExists(user: User): Boolean {
        if (!user.isValid()) return false
        return userDAO.userExists(user.username)
    }

    fun isBanned(username: String): Boolean {
        //this is clever, impressed myself
        return userDAO.isBanned(username) == 1
    }

    fun userExists(username: String): Boolean {
        return userDAO.userExists(username)
    }

    fun hasAdminRights(username: String): Boolean {
        return GroupHandler.userInGroup(username, "admins")
    }
}
