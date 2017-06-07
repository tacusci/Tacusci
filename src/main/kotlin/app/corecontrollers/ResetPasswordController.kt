/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

package app.corecontrollers

import api.core.TacusciAPI
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
import utils.Validation
import utils.j2htmlPartials
import java.util.*

/**
 * Created by alewis on 12/03/2017.
 */
class ResetPasswordController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/reset_password"
    override val childUris: MutableList<String> = mutableListOf("/:username", "/:username/:authhash")
    override val templatePath: String = "/templates/reset_password.vtl"
    override val pageTitleSubstring: String = "Reset Password"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    private val resetPasswordDAO = DAOManager.getDAO(DAOManager.TABLE.RESET_PASSWORD) as ResetPasswordDAO
    private val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO

    override fun initSessionBoolAttributes(session: Session) {
        hashMapOf(Pair("new_password_field_error", false), Pair("new_password_repeated_field_error", false), Pair("passwords_dont_match", false),
                    Pair("reset_password_successfully", false)).forEach { key, value -> if (!session.attributes().contains(key)) session.attribute(key, value)}
    }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("template", templatePath)
        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavBar(request, model)
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for RESET_PASSWORD/${request.params(":username")} page")

        var username = request.params(":username")
        val authHash = request.params(":authhash")

        if (username == null && UserHandler.isLoggedIn(request)) username = UserHandler.loggedInUsername(request)

        if (username != null) {
            if (authHash == null) {
                if (UserHandler.isLoggedIn(request) && UserHandler.loggedInUsername(request) == username) {
                    val newAuthHash = UserHandler.updateResetPasswordHash(username)
                    response.managedRedirect(request, "$rootUri/$username/$newAuthHash")
                } else {
                    logger.info("${UserHandler.getSessionIdentifier(request)} -> Received unauthorised reset password request for user $username")
                    genAccessDeniedContent(request, model)
                }
            } else {
                val userId = userDAO.getUserID(username)
                if (resetPasswordDAO.authHashExists(userId)) {
                    if (resetPasswordDAO.getAuthHash(userId) == authHash) {
                        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received authorised reset password request for user $username")

                        //if authhash has gone past expired time limit, mark as expired
                        if (checkAuthHashExpired(authHash)) resetPasswordDAO.updateAuthHash(userId, resetPasswordDAO.getAuthHash(userId), 1)

                        //check if the authhash is marked as expired
                        if (!resetPasswordDAO.authHashExpired(authHash)) {
                            genResetPasswordPageContent(request, response, username, model, authHash)
                        } else {
                            genAccessExpiredContent(request, model)
                        }
                    } else {
                        response.managedRedirect(request, "$rootUri/$username")
                    }
                } else {
                    response.managedRedirect(request, "$rootUri/$username")
                }
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun genResetPasswordPageContent(request: Request, response: Response, username: String, model: HashMap<String, Any>, authHash: String) {
        TacusciAPI.injectAPIInstances(request, response, model)
        val resetPasswordForm = j2htmlPartials.pureFormAligned_ResetPassword(request.session(), "reset_password_form", username, "$rootUri/$username/$authHash", "post")
        val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        val resetPasswordDAO = DAOManager.getDAO(DAOManager.TABLE.RESET_PASSWORD) as ResetPasswordDAO
        model.put("reset_password_form", h1("Reset Password").render() + resetPasswordForm.render())
        if (request.session().attribute("reset_password_successfully")) {
            model.put("password_reset_successful", h2("Password reset successfully"))
            request.session().attribute("reset_password_successfully", false)
            val userId = userDAO.getUserID(username)
            //update hash to mark it as expired
            resetPasswordDAO.updateAuthHash(userId, resetPasswordDAO.getAuthHash(userId), 1)
        } else {
            if (request.session().attribute("passwords_dont_match")) {
                model.put("passwords_dont_match", h2("Passwords don't match"))
                request.session().attribute("passwords_dont_match", true)
            }
        }
    }

    fun genAccessDeniedContent(request: Request, model: HashMap<String, Any>) {
        Web.loadNavBar(request, model)
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Tried accessing someone's reset password form")
        model.put("unauthorised_reset_request_message", h1("Access Denied"))
    }

    fun genAccessExpiredContent(request: Request, model: HashMap<String, Any>) {
        Web.loadNavBar(request, model)
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Tried accessing an expired reset password form address")
        model.put("access_expired_message", h1("Access has expired"))
    }

    private fun post_resetPassword(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for reset password form")

        if (Web.getFormHash(request, "reset_password_form") == request.queryParams("hashid")) {
            val username = request.params(":username")
            val authHash = request.params(":authhash")

            if (username != null && authHash != null) {
                val userId = userDAO.getUserID(username)
                if (resetPasswordDAO.authHashExists(userId)) {
                    if (checkAuthHashExpired(authHash)) { resetPasswordDAO.updateAuthHash(userId, authHash, 1) }
                }
            }

            if (!resetPasswordDAO.authHashExpired(authHash)) {
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
                            } else request.session().attribute("reset_password_successfully", false)
                        }
                    } else {
                        request.session().attribute("reset_password_successfully", false)
                        request.session().attribute("passwords_dont_match", true)
                    }
                }
            } else {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> Recieved POST submission for expired reset password form")
            }
        }
        response.managedRedirect(request, request.uri())
        return response
    }

    override fun post(request: Request, response: Response): Response {
        when (request.queryParams("formName")) {
            "reset_password_form" -> return post_resetPassword(request, response)
        }
        return response
    }

    private fun checkAuthHashExpired(authHash: String): Boolean {
        var timeoutInSeconds = 1000
        val secondsTimeout = Config.getProperty("reset_password_authhash_timeout_seconds")
        try {
            timeoutInSeconds *= Integer.parseInt(secondsTimeout)
        } catch (e: Exception) {
            timeoutInSeconds = 60000
            logger.error("Reset password timeout setting has not been set, defaulting to 60 seconds")
        }

        if (System.currentTimeMillis() - resetPasswordDAO.getLastUpdatedDateTime(authHash) > timeoutInSeconds) return true

        return false
    }
}