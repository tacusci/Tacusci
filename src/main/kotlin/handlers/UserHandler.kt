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

import com.sun.org.apache.xpath.internal.operations.Bool
import database.daos.DAOManager
import database.daos.UserDAO
import database.models.User
import extensions.forwardedIP
import mu.KLogging
import spark.Request
import spark.Session
import utils.Config
import utils.PasswordStorage

/**
 * Created by tauraamui on 24/10/2016.
 */

object  UserHandler : KLogging() {

    val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO

    fun login(request: Request, username: String, password: String): Boolean {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Attempting to login $username")

        val session = request.session()

        if (UserHandler.isBanned(username)) {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> User $username is banned, denying login")
            session.attribute("is_banned", true)
            session.attribute("banned_username", username)
            return false
        } else {
            session.attribute("is_banned", false)
            session.attribute("banned_username", "")
        }

        val authHash = userDAO.getUserAuthHash(username)

        if (authHash.isNotBlank() && authHash.isNotEmpty()) {
            if (PasswordStorage.verifyPassword(password, authHash)) {
                session.attribute("logged_in", true)
                session.attribute("username", username)
                session.attribute("login_incorrect_creds", false)
                logger.info("Login successful")
            } else {
                session.attribute("login_incorrect_creds", true)
                logger.info("Login unsuccessful, incorrect password...")
                return false
            }
        } else {
            session.attribute("login_incorrect_creds", true)
            logger.info("Login unsuccessful, username not recognised")
            return false
        }
        return true
    }

    fun logout(request: Request) {
        val session = request.session()
        session.removeAttribute("logged_in")
        session.removeAttribute("username")
        session.attributes().clear()
    }

    fun isLoggedIn(request: Request): Boolean {
        val session = request.session()
        if (session.attributes().isNotEmpty()) {
            if (session.attributes().contains("logged_in")) {
                val loggedIn: Boolean = session.attribute("logged_in")
                return loggedIn
            }
        }
        return false
    }

    fun loggedInUsername(request: Request): String {
        val session = request.session()
        if (isLoggedIn(request)) {
            if (session.attributes().contains("username")) {
                return session.attribute("username")
            }
        }
        return ""
    }

    fun createRootAdmin(): Boolean {
        val rootAdmin = User(Config.getProperty("default_admin_user"), Config.getProperty("default_admin_user"), Config.getProperty("default_admin_password"), Config.getProperty("default_admin_email"), 0, 1)
        if (!rootAdmin.isValid()) return false
        userDAO.insertUser(rootAdmin)

        if ((userDAO.getRootAdmin().username != rootAdmin.username) || userDAO.getRootAdmin().password != PasswordStorage.createHash(rootAdmin.password)) {
            if (userDAO.updateRootAdmin(rootAdmin)) {
                logger.info("Root admin has been updated from properties...")
            } else {
                logger.info("Root admin has been changed in properties file but update has failed...")
            }
        }

        GroupHandler.addUserToGroup(rootAdmin, "members")
        GroupHandler.addUserToGroup(rootAdmin, "admins")
        return true
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

    fun getRootAdmin(): User {
        return userDAO.getRootAdmin()
    }

    fun ban(username: String): Boolean {
        return userDAO.ban(username)
    }

    fun unban(username: String): Boolean {
        return userDAO.unban(username)
    }

    fun isBanned(username: String): Boolean {
        //this is clever, impressed myself
        return userDAO.isBanned(username) == 1
    }

    fun userExists(username: String): Boolean {
        return userDAO.userExists(username)
    }

    fun getSessionIdentifier(request: Request): String {
        //FIXME (something in this method is universally causing a silent error)
        var clientIP = request.forwardedIP()
        if (clientIP.isEmpty() || clientIP.isBlank()) { clientIP = request.ip() }
        return if (UserHandler.isLoggedIn(request)) "$clientIP | ${UserHandler.loggedInUsername(request)}" else clientIP
    }
}
