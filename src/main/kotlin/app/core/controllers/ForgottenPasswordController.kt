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

package app.core.core.controllers

import api.core.TacusciAPI
import app.core.controllers.Controller
import app.core.handlers.UserHandler
import database.models.User
import extensions.managedRedirect
import mail.Email
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Config
import utils.Utils
import utils.j2htmlPartials
import java.io.File
import kotlin.concurrent.thread

/**
 * Created by alewis on 07/04/2017.
 */
class ForgottenPasswordController : Controller {

    companion object : KLogging()

    override var rootUri = "/forgotten_password"
    override var childUris = mutableListOf<String>()
    override val templatePath: String = "/templates/forgotten_password.vtl"
    override val pageTitleSubstring: String = "Forgotten Password"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) { hashMapOf(Pair("email_sent", false)).forEach { key, value -> if (!session.attributes().contains(key)) session.attribute(key, value) } }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for forgotten password page")
        val model = hashMapOf<String, Any>()

        TacusciAPI.injectAPIInstances(request, response, model)

        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavBar(request, model)

        if (UserHandler.isLoggedIn(request)) {
            response.managedRedirect(request, "/reset_password")
        }

        model.put("template", templatePath)


        val forgottenPasswordForm = j2htmlPartials.pureFormAligned_ForgottenPassword(request.session(), "forgotten_password_form", rootUri, "post")

        if (request.session().attribute("email_sent")) {
            model.put("sent_email_message", j2htmlPartials.centeredMessage("Email has been sent", j2htmlPartials.HeaderType.h2).render())
            request.session().attribute("email_sent", false)
        } else {
            model.put("forgotten_password_form", forgottenPasswordForm.render())
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun post_postForgottenPassword(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for forgotten password form")
        if (Web.getFormHash(request, "forgotten_password_form") == request.queryParams("hashid")) {
            val username = request.queryParams("username")
            val email = request.queryParams("email")

            if (username != null && email != null) {
                if (UserHandler.userExists(username)) {
                    val user = UserHandler.userDAO.getUser(username)
                    if (user.email == email) {
                        sendResetPasswordEmail(user, request)
                    }
                }
            }
            request.session().attribute("email_sent", true)
        } else {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> Received invalid POST form for forgotten password")
        }
        response.managedRedirect(request, request.uri())
        return response
    }

    private fun sendResetPasswordEmail(user: User, request: Request) {
        //change the latest reset password hash in the DB and append it to the address to send
        var resetPasswordLink = "${request.url().replace(request.uri(), "")}/reset_password/${user.username}/${UserHandler.updateResetPasswordHash(user.username)}"
        if (Config.getProperty("using_ssl_on_proxy").toBoolean()) {
            resetPasswordLink = resetPasswordLink.replace("http", "https")
        }

        val emailContentFile = File(Config.getProperty("reset_password_email_content_file"))
        var emailContent = "${Utils.getDateTimeNow()} $resetPasswordLink"

        if (emailContentFile.exists()) {
            emailContent = emailContentFile.readText().replace("\$reset_password_link", resetPasswordLink)
            emailContent = emailContent.replace("\$username", user.username)
            emailContent = emailContent.replace("\$time_stamp", Utils.getDateTimeNow())
        }


        thread { Email.sendEmail(mutableListOf(user.email), Config.getProperty("reset_password_from_address"), Config.getProperty("reset_password_email_subject"), emailContent) }
    }

    override fun post(request: Request, response: Response): Response {
        when (request.queryParams("formName")) {
            "forgotten_password_form" -> return post_postForgottenPassword(request, response)
        }
        response.managedRedirect(request, request.uri())
        return response
    }
}