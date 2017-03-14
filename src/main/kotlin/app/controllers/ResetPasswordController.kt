package app.controllers

import app.handlers.UserHandler
import database.daos.DAOManager
import database.daos.ResetPasswordDAO
import database.daos.UserDAO
import extensions.managedRedirect
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Utils
import java.util.*

/**
 * Created by alewis on 12/03/2017.
 */
class ResetPasswordController : Controller {

    companion object : KLogging()

    override fun initSessionAttributes(session: Session) {}

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for RESET_PASSWORD/${request.params(":username")} page")

        val username = request.params(":username")
        val authHash = request.params(":authhash")

        val resetPasswordDAO = DAOManager.getDAO(DAOManager.TABLE.RESET_PASSWORD) as ResetPasswordDAO
        val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO

        if (authHash == null) {
            if (UserHandler.isLoggedIn(request) && UserHandler.loggedInUsername(request) == username) {
                val newAuthHash = Utils.randomHash()
                val userId = userDAO.getUserID(username)
                if (resetPasswordDAO.authHashExists(userId)) resetPasswordDAO.updateAuthHash(userId, newAuthHash) else resetPasswordDAO.insertAuthHash(userId, newAuthHash)
                response.managedRedirect(request, "/reset_password/$username/$newAuthHash")
            }
        } else {
            if (userDAO.userExists(username)) {
                val userId = userDAO.getUserID(username)
                if (resetPasswordDAO.authHashExists(userId)) {
                    if (resetPasswordDAO.getAuthHash(userId) == authHash) {
                        model.put("title", "Reached reset password page")
                    } else {
                        model.put("title", "Incorrect authhash")
                    }
                } else {
                    model.put("title", "Incorrect authhash")
                }
            } else {
                model.put("title", "Unrecognised username")
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}