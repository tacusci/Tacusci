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
import db.daos.UserDAO
import db.models.User
import db.models.isValid
import mu.KLogging
import spark.Session
import utils.Config

/**
 * Created by tauraamui on 24/10/2016.
 */

object  UserHandler : KLogging() {

    fun login(session: Session, username: String, password: String): Boolean {
        logger.info("Attempting to login $username")
        val usersDAO: UserDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO

        val authHash = usersDAO.getUserAuthHash(username)

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

    fun isInGroup(username: String, groupName: String): Boolean {
        if (username.toLowerCase() == "tauraamui") {
            if (groupName.toLowerCase() == "administrators") {
                return true
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
        val defaultRootUser = User(Config.getProperty("default_admin_user"), Config.getProperty("default_admin_user"), Config.getProperty("default_admin_password"), Config.getProperty("default_admin_email"), 0)
        return createUser(defaultRootUser)
    }

    fun createUser(user: User): Boolean {
        if (!user.isValid()) return false
        val usersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        usersDAO.insertUser(user)
        return true
    }

    fun userExists(username: String): Boolean {
        val usersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        return usersDAO.userExists(username)
    }
}
