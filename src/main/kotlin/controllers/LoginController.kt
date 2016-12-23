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

import db.daos.DAOManager
import db.daos.UserDAO
import db.handlers.UserHandler
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*

/**
 * Created by alewis on 27/10/2016.
 */

object LoginController: KLogging() {

    fun get_login(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("Received GET request for LOGIN page")
        Web.initSessionAttributes(request.session())
        val model = HashMap<String, Any>()
        if (!UserHandler.isLoggedIn(request.session())) {
            logger.info("Current user session is not logged in, giving default login page")
            model.put("template", "/templates/login.vtl")
            model.put("title", "Thames Valley Furs - Login")
            val loginError: Boolean = request.session().attribute("login_error")
            if (loginError) {
                logger.info("Detected previous login attempt error, altering page to include error message")
                model.put("login_error", "<p>Username or password incorrect...</p>")
                request.session().attribute("login_error", false)
            } else {
                model.put("login_error", "<br>")
            }
        } else {
            logger.info("User already logged in, redirecting to landing page")
            response.redirect("/dashboard")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun post_postLogin(request: Request, response: Response): Response {
        logger.info("Received POST submission for LOGIN page")
        Web.initSessionAttributes(request.session())
        var username = request.queryParams("username")
        var email = ""
        val password = request.queryParams("password")

        if (!(username.isNullOrBlank() || username.isNullOrEmpty() || password.isNullOrBlank() || password.isNullOrEmpty())) {
            //TODO: Need to improve email validation
            if (username.contains("@")) {
                logger.info("Email instead of username detected, fetching associated username")
                email = username
                val userDAO: UserDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
                username = userDAO.getUsernameFromEmail(email)
            }
            UserHandler.login(request.session(), username, password)
        } else {
            request.session().attribute("login_error", true)
            logger.info("Unrecognised username/password provided in form")
        }
        logger.info("Redirecting to login page")
        response.redirect("/login")
        return response
    }

    fun post_logout(request: Request, response: Response): Response {
        if (UserHandler.isLoggedIn(request.session())) {
            UserHandler.logout(request.session())
        }
        response.redirect("/")
        return response
    }
}