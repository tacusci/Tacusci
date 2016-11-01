import db.DAOManager
import db.UsersDAO
import db.models.NewUser
import db.models.isValid
import javafx.scene.control.Tab
import spark.Session
import java.security.InvalidParameterException

/**
 * Created by tauraamui on 24/10/2016.
 */

object  UserHandler {

    fun login(session: Session, username: String, password: String): Boolean {
        val usersDAO: UsersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UsersDAO
        val authHash = usersDAO.getUserAuthHash(username)
        if (PasswordStorage.verifyPassword(password, authHash)) {
            session.attribute("logged_in", true)
            session.attribute("username", username)
            session.attribute("login_error", false)
        } else {
            session.attribute("login_error", true)
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

    fun isLoggedIn(session: Session, username: String): Boolean {
        if (session.attributes().isNotEmpty()) {
            if (session.attributes().contains("logged_in")) {
                val loggedIn: Boolean = session.attribute("logged_in")
                val usernameInSession: String = session.attribute("username")
                if (username.toLowerCase() == usernameInSession.toLowerCase()) return loggedIn
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

    fun createUser(newUser: NewUser): Boolean {
        if (!newUser.isValid()) return false
        val usersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UsersDAO
        usersDAO.insertUser(newUser)
        return true
    }
}
