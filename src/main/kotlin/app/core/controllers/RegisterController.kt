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

package app.core.controllers

import api.core.TacusciAPI
import app.core.Web
import app.core.handlers.UserHandler
import database.models.User
import extensions.managedRedirect
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
 * Created by alewis on 02/02/2017.
 */

class RegisterController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/register"
    override val childGetUris: MutableList<String> = mutableListOf()
    override val childPostUris: MutableList<String> = mutableListOf()
    override val templatePath: String = "/templates/register.vtl"
    override val pageTitleSubstring: String = "Sign Up"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {
        hashMapOf(Pair("full_name_field_error", false), Pair("username_field_error", false), Pair("password_field_error", false),
                Pair("repeated_password_field_error", false), Pair("email_field_error", false), Pair("username_not_available_error", false),
                Pair("username_not_available", ""), Pair("user_created_successfully", false), Pair("passwords_mismatch_error", false)).forEach { key, value -> if (!session.attributes().contains(key)) session.attribute(key, value) }
    }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for REGISTER page")

        if (!Config.getProperty("allow-signup").toBoolean()) {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> Sign up/register is not enabled, hiding REGISTER page behind 404...")
            return Web.gen_404Page(request, response, layoutTemplate)
        }

        val model = HashMap<String, Any>()

        TacusciAPI.injectAPIInstances(request, response, model)

        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavigationElements(request, model)

        model.put("template", templatePath)
        model.put("register_form", j2htmlPartials.pureFormAligned_Register(request.session(), "register_form", rootUri, "post").render())

        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for REGISTER page")

        if (Web.getFormHash(request, "register_form") == request.queryParams("hashid")) {

            if (!Config.getProperty("allow-signup").toBoolean()) {
                val fullName = request.queryParams("full_name")
                val username = request.queryParams("username")
                val password = request.queryParams("password")
                val repeatedPassword = request.queryParams("repeat_password")
                val email = request.queryParams("email")

                request.session().attribute("user_created_successfully", false)

                val fullNameInputIsValid = Validation.matchFullNamePattern(fullName)
                val usernameInputIsValid = Validation.matchUsernamePattern(username)
                val passwordInputIsValid = Validation.matchPasswordPattern(password)
                val repeatedPasswordIsValid = Validation.matchPasswordPattern(repeatedPassword)
                val emailIsValid = Validation.matchEmailPattern(email)

                if (!fullNameInputIsValid) request.session().attribute("full_name_field_error", true) else request.session().attribute("full_name_field_error", false)
                if (!usernameInputIsValid) request.session().attribute("username_field_error", true) else request.session().attribute("username_field_error", false)
                if (!passwordInputIsValid) request.session().attribute("password_field_error", true) else request.session().attribute("password_field_error", false)
                if (!repeatedPasswordIsValid) request.session().attribute("repeated_password_field_error", true) else request.session().attribute("repeated_password_field_error", false)
                if (!emailIsValid) request.session().attribute("email_field_error", true) else request.session().attribute("email_field_error", false)

                //TODO: MUST ADD CHECK FOR PREEXISTING EMAIL

                if (usernameInputIsValid) {
                    if (UserHandler.userExists(username)) {
                        request.session().attribute("username_not_available_error", true)
                        request.session().attribute("username_not_available", username)
                    }
                }

                if (passwordInputIsValid && repeatedPasswordIsValid) {
                    if (password != repeatedPassword) request.session().attribute("passwords_mismatch_error", true) else request.session().attribute("passwords_mismatch_error", false)
                }

                if (fullNameInputIsValid && usernameInputIsValid && passwordInputIsValid && repeatedPasswordIsValid && emailIsValid && (password == repeatedPassword)) {
                    val user = User(-1, -1, -1, fullName, username, password, email, 0, false)
                    request.session().attribute("user_created_successfully", true)
                    UserHandler.createUser(user)
                    sendConfirmationEmail(user)
                }
            } else {
                logger.warn("${UserHandler.getSessionIdentifier(request)} -> has submitted a valid register form, but user side signups have been disabled")
            }
        } else {
            logger.warn("${UserHandler.getSessionIdentifier(request)} -> has submitted an invalid register form...")
        }
        response.managedRedirect(request, rootUri)
        return response
    }

    //TODO: Implement this
    private fun sendConfirmationEmail(user: User) {}
}