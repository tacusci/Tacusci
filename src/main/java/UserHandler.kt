import spark.Session

/**
 * Created by tauraamui on 24/10/2016.
 */

object  UserHandler {

    fun login(session: Session, username: String, password: String) {
        if (username.toLowerCase() == "tauraamui" && password == "placeholder") {
            session.attribute("logged_in", true)
            session.attribute("username", username)
        } else {
            session.attribute("login_error", true)
        }
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
}
