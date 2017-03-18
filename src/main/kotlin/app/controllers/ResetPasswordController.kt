package app.controllers

import app.handlers.UserHandler
import database.daos.DAOManager
import database.daos.ResetPasswordDAO
import database.daos.UserDAO
import extensions.managedRedirect
import j2html.TagCreator.h1
import j2html.TagCreator.h2
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Config
import utils.Utils
import utils.Validation
import utils.j2htmlPartials
import java.util.*

/**
 * Created by alewis on 12/03/2017.
 */
class ResetPasswordController : Controller {

    companion object : KLogging()

    override fun initSessionBoolAttributes(session: Session) {
        hashMapOf(Pair("new_password_field_error", false), Pair("new_password_repeated_field_error", false),
                    Pair("reset_password_successfully", false)).forEach { key, value -> if (!session.attributes().contains(key)) session.attribute(key, value)}
    }

    fun genResetPasswordPageContent(request: Request, username: String, model: HashMap<String, Any>, authHash: String) {
        Web.loadNavBar(request, model)
        val resetPasswordForm = j2htmlPartials.pureFormAligned_ResetPassword(request.session(), "reset_password_form", username, "/reset_password/$username/$authHash", "post")
        val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        val resetPasswordDAO = DAOManager.getDAO(DAOManager.TABLE.RESET_PASSWORD) as ResetPasswordDAO
        model.put("reset_password_form", h1("Reset Password").render() + resetPasswordForm.render())
        if (request.session().attribute("reset_password_successfully")) {
            model.put("password_reset_successful", h2("Password reset successfully"))
            request.session().attribute("reset_password_successfully", false)
            val userId = userDAO.getUserID(username)
            resetPasswordDAO.updateAuthHash(userId, resetPasswordDAO.getAuthHash(userId), 1)
        }
    }

    fun genAccessDeniedContent(request: Request, model: HashMap<String, Any>) {
        Web.loadNavBar(request, model)
        model.put("unauthorised_reset_request_message", h1("Access Denied"))
    }

    fun genAccessExpiredContent(request: Request, model: HashMap<String, Any>) {
        Web.loadNavBar(request, model)
        model.put("access_expired_message", h1("Access has expired"))
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
                        resetPasswordDAO.updateAuthHash(userId, newAuthHash, 0)
                    } else {
                        resetPasswordDAO.insertAuthHash(userId, newAuthHash)
                    }
                    response.managedRedirect(request, "/reset_password/$username/$newAuthHash")
                } else {
                    logger.info("${UserHandler.getSessionIdentifier(request)} -> Received unauthorised reset password request for user $username")
                    genAccessDeniedContent(request, model)
                }
            } else {
                if (resetPasswordDAO.authHashExists(userDAO.getUserID(username))) {
                    if (resetPasswordDAO.getAuthHash(userDAO.getUserID(username)) == authHash) {
                        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received authorised reset password request for user $username")
                        if (!resetPasswordDAO.authHashExpired(authHash)) {
                            genResetPasswordPageContent(request, username, model, authHash)
                        } else {
                            genAccessExpiredContent(request, model)
                        }
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

    private fun post_resetPassword(request: Request, response: Response): Response {
        if (Web.getFormHash(request.session(), "reset_password_form") == request.queryParams("hashid")) {
            val usernameOfPasswordToReset = request.queryParams("username")
            val newPassword = request.queryParams("new_password")
            val newPasswordRepeated = request.queryParams("new_password_repeated")

            val newPasswordInputIsValid = Validation.matchPasswordPattern(newPassword)
            val newRepeatedPasswordIsValid = Validation.matchPasswordPattern(newPasswordRepeated)

            if (!newPasswordInputIsValid) request.session().attribute("new_password_field_error", true) else request.session().attribute("new_password_field_error", false)
            if (!newRepeatedPasswordIsValid) request.session().attribute("new_password_repeated_field_error", true) else request.session().attribute("new_password_repeated_field_error", false)

            if (newPasswordInputIsValid && newRepeatedPasswordIsValid) {
                if (newPassword == newPasswordRepeated) {
                    if (usernameOfPasswordToReset == UserHandler.getRootAdmin().username) {
                        Config.props.setProperty("default_admin_password", newPassword)
                        Config.storeAll()
                        if (UserHandler.updateRootAdmin()) {
                            logger.info("${UserHandler.getSessionIdentifier(request)} -> Password for $usernameOfPasswordToReset has been reset/changed...")
                            request.session().attribute("reset_password_successfully", true)
                        } else request.attribute("reset_password_successfully", false)
                    } else {
                        val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
                        val userToUpdate = userDAO.getUser(usernameOfPasswordToReset)
                        userToUpdate.password = newPassword
                        if (userDAO.updateUser(userToUpdate)) {
                            logger.info("${UserHandler.getSessionIdentifier(request)} -> Password for $usernameOfPasswordToReset has been reset/changed...")
                            request.session().attribute("reset_password_successfully", true)
                        } else request.attribute("reset_password_successfully", false)
                    }
                }
            }
            response.managedRedirect(request, request.uri())
        }
        return response
    }

    override fun post(request: Request, response: Response): Response {
        when (request.queryParams("formName")) {
            "reset_password_form" -> return post_resetPassword(request, response)
        }
        return response
    }
}