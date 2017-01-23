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

import handlers.UserHandler
import j2html.TagCreator.*
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.j2htmlPartials
import java.util.*

/**
 * Created by alewis on 27/10/2016.
 */

object LoginController : KLogging() {

    fun get_login(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for LOGIN page")
        Web.initSessionAttributes(request.session())
        var model = HashMap<String, Any>()
        model = Web.loadNavBar(request, response, model)

        if (UserHandler.isLoggedIn(request)) {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> User already logged in, redirecting to landing page")
            response.redirect("/")
        }

        model.put("template", "/templates/login.vtl")
        model.put("title", "Thames Valley Furs - Login")

        val loginForm = j2htmlPartials.pureFormAligned_Login(request.session(), "login_form", "/login", "post")

        if (request.session().attribute("login_incorrect_creds")) {
            request.session().attribute("login_incorrect_creds", false)
            model.put("username_or_password_incorrect", p("Username or password is incorrect...").withClass("error-text"))
        }

        model.put("login_form", h1("Login").render()+loginForm.render())

        if (request.session().attribute("is_banned")) {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> User ${request.session().attribute<String>("banned_username")} is banned")
            model.put("banned_message", img().withSrc("/images/you_have_been_banned.jpg"))
            model.put("login_form", "")
            model.put("signup_link", "")
            request.session().attribute("is_banned", false)
            request.session().attribute("banned_username", "")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun post_postLogin(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for LOGIN page")
        Web.initSessionAttributes(request.session())

        if (Web.getFormHash(request.session(), "login_form") == request.queryParams("hashid")) {
            var username = request.queryParams("username").toLowerCase()
            var email = ""
            val password = request.queryParams("password")

            if (!(username.isNullOrBlank() || username.isNullOrEmpty() || password.isNullOrBlank() || password.isNullOrEmpty())) {
                //TODO: Need to improve email validation
                if (username.contains("@")) {
                    logger.info("${UserHandler.getSessionIdentifier(request)} -> Email instead of username detected, fetching associated username")
                    email = username
                    username = UserHandler.userDAO.getUsernameFromEmail(email)
                }
                UserHandler.login(request, username, password)
            } else {
                request.session().attribute("login_error", true)
                logger.info("Unrecognised username/password provided in form")
            }
        } else {
            logger.warn("${UserHandler.getSessionIdentifier(request)} -> has submitted an invalid login form...")
        }

        logger.info("${UserHandler.getSessionIdentifier(request)} -> Redirecting to login page")
        response.redirect("/login")
        return response
    }

    fun post_logout(request: Request, response: Response): Response {
        if (UserHandler.isLoggedIn(request)) {
            UserHandler.logout(request)
        }
        response.redirect("/")
        return response
    }
}