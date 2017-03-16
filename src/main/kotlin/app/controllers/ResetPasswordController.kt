package app.controllers

import app.handlers.UserHandler
import database.daos.DAOManager
import database.daos.ResetPasswordDAO
import database.daos.UserDAO
import extensions.managedRedirect
import j2html.TagCreator.h1
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Utils
import utils.j2htmlPartials
import java.util.*

/**
 * Created by alewis on 12/03/2017.
 */
class ResetPasswordController : Controller {

    companion object : KLogging()

    override fun initSessionAttributes(session: Session) {}

    fun generateResetPasswordPageContent(request: Request, username: String, model: HashMap<String, Any>, authHash: String) {
        if (UserHandler.isLoggedIn(request) && UserHandler.loggedInUsername(request) == username) {
            Web.loadNavBar(request, model)
        }
        val resetPasswordForm = j2htmlPartials.pureFormAligned_ResetPassword(request.session(), "reset_password_form", "/reset_password/$username/$authHash", "post")
        model.put("reset_password_form", h1("Reset Password").render()+resetPasswordForm.render())
    }

    fun generateAccessDeniedContent(request: Request, model: HashMap<String, Any>) {
        model.put("unauthorised_reset_request_message", h1("Access Denied"))
    }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("template", "/templates/reset_password.vtl")
        model.put("title", "Thames Valley Furs - Reset password")
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for RESET_PASSWORD/${request.params(":username")} page")

        val username = request.params(":username")
        val authHash = request.params(":authhash")

        val resetPasswordDAO = DAOManager.getDAO(DAOManager.TABLE.RESET_PASSWORD) as ResetPasswordDAO
        val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO

        if (username != null) {
            if (authHash == null) {
                if (UserHandler.isLoggedIn(request) && UserHandler.loggedInUsername(request) == username) {
                    val newAuthHash = Utils.randomHash()
                    val userId = userDAO.getUserID(username)
                    if (resetPasswordDAO.authHashExists(userId)) {
                        resetPasswordDAO.updateAuthHash(userId, newAuthHash)
                    } else {
                        resetPasswordDAO.insertAuthHash(userId, newAuthHash)
                    }
                    response.managedRedirect(request, "/reset_password/$username/$newAuthHash")
                } else {
                    logger.info("${UserHandler.getSessionIdentifier(request)} -> Received unauthorised reset password request for user $username")
                    generateAccessDeniedContent(request, model)
                }
            } else {
                if (resetPasswordDAO.authHashExists(userDAO.getUserID(username))) {
                    if (resetPasswordDAO.getAuthHash(userDAO.getUserID(username)) == authHash) {
                        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received authorised reset password request for user $username")
                        generateResetPasswordPageContent(request, username, model, authHash)
                    } else {
                        response.managedRedirect(request, "/reset_password/$username")
                    }
                } else {
                    response.managedRedirect(request, "/reset_password/$username")
                }
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        if (Web.getFormHash(request.session(), "reset_password_form") == request.queryParams("hashid")) {}
        return response
    }
}