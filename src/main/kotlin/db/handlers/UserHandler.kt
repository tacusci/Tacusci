package db.handlers

import db.daos.DAOManager
import db.daos.UserDAO
import db.models.User
import db.models.isValid
import mu.KLogging
import spark.Session

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

    fun createUser(user: User): Boolean {
        if (!user.isValid()) return false
        val usersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        usersDAO.insertUser(user)
        return true
    }

    fun userExists(user: User): Boolean {
        if (!user.isValid()) return false
        val usersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        return usersDAO.userExists(user.username)
    }
}
