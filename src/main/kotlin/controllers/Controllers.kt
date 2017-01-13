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
 
 
 
 package controllers

import com.sun.org.apache.xpath.internal.operations.Bool
import database.daos.DAOManager
import database.daos.UserDAO
import handlers.UserHandler
import database.models.*
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.j2htmlPartials
import java.util.*

/**
 * Created by alewis on 25/10/2016.
 */

object Web : KLogging() {

    //TODO: Move most of these to their own controller classes :3

    val sessionAttributes = hashMapOf(Pair("login_incorrect_creds", false), Pair("is_banned", false), Pair("username", ""))

    fun initSessionAttributes(session: Session) {
        sessionAttributes.forEach { pair -> if (!session.attributes().contains(pair.key)) session.attribute(pair.key, pair.value) }
    }

    fun post_createPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for CREATE_PAGE page")
        val model = HashMap<String, Any>()
        model.put("template", "/templates/create_page.vtl")
        model.put("title", "Thames Valley Furs - Create page")
        return ModelAndView(model, layoutTemplate)
    }

    fun get_register(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for REGISTER page")
        val sessionAttributes = hashMapOf(Pair("full_name_field_error", false),
                                        Pair("username_field_error",false),
                                        Pair("password_field_error", false),
                                        Pair("email_field_error", false),
                                        Pair("username_not_available_error", false),
                                        Pair("username_not_available", ""))

        val model = HashMap<String, Any>()
        model.put("template", "/templates/register.vtl")
        model.put("title", "Thames Valley Furs - Sign Up")
        model.put("full_name_error_hidden", "hidden")
        model.put("username_error_hidden", "hidden")
        model.put("password_error_hidden", "hidden")
        model.put("email_error_hidden", "hidden")
        model.put("username_not_available_hidden", "hidden")

        sessionAttributes.forEach { attribute, defaultValue ->
            if (!request.session().attributes().contains(attribute)) {
                request.session().attribute(attribute, defaultValue)
            }
        }

        if (request.session().attribute("full_name_field_error")) { model.put("full_name_error_hidden", "") }
        if (request.session().attribute("username_field_error")) { model.put("username_error_hidden", "") }
        if (request.session().attribute("password_field_error")) { model.put("password_error_hidden", "") }
        if (request.session().attribute("email_field_error")) { model.put("email_error_hidden", "") }
        if (request.session().attribute("username_not_available_error")) {
            model.put("username_not_available_hidden", "")
            model.put("unavailable_username", request.session().attribute("username_not_available"))
        }

        sessionAttributes.forEach { attribute, defaultValue -> request.session().removeAttribute(attribute) }

        model.put("register_form", j2htmlPartials.pureFormAligned_Register("/register", "post").render())

        return ModelAndView(model, layoutTemplate)
    }

    fun post_register(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for REGISTER page")
        val model = HashMap<String, Any>()
        val fullName = request.queryParams("full_name")
        val username = request.queryParams("username")
        val password = request.queryParams("password")
        val email = request.queryParams("email")

        model.put("full_name_error_hidden", true)

        val user = User(fullName, username, password, email, 0)

        if (!UserHandler.userDAO.userExists(user.username)) {
            if (UserHandler.createUser(user)) {
                response.redirect("/login")
            } else {
                if (!user.isFullnameValid()) {
                    request.session().attribute("full_name_field_error", true)
                }
                if (!user.isUsernameValid()) {
                    request.session().attribute("username_field_error", true)
                }
                if (!user.isPasswordValid()) {
                    request.session().attribute("password_field_error", true)
                }
                if (!user.isEmailValid()) {
                    request.session().attribute("email_field_error", true)
                }
                response.redirect("/register")
            }
        } else {
            request.session().attribute("username_not_available_error", true)
            request.session().attribute("username_not_available", user.username)
            response.redirect("/register")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun get_accessDeniedPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("title", "Thames Valley Furs - Dashboard (access denied)")
        model.put("template", "/templates/access_denied.vtl")
        return ModelAndView(model, layoutTemplate)
    }

    fun get_userNotFound(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("title", "Thames Valley Furs - Profile (User not found)")
        model.put("template", "/templates/user_not_found.vtl")
        return ModelAndView(model, layoutTemplate)
    }
}