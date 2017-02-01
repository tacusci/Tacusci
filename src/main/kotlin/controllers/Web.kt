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

import database.models.User
import handlers.GroupHandler
import handlers.UserHandler
import j2html.TagCreator.pre
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Validation
import utils.j2htmlPartials
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import javax.validation.Valid

/**
 * Created by alewis on 25/10/2016.
 */

object Web : KLogging() {

    //TODO: Move most of these to their own controller classes :3

    fun loadNavBar(request: Request, response: Response, model: HashMap<String, Any>): HashMap<String, Any> {
        model.put("home_link", j2htmlPartials.pureMenuItemLink("/", "Home").render())
        model.put("login_or_profile_link", j2htmlPartials.pureMenuItemLink("/login", "Login").render())
        model.put("sign_up_menu_link", j2htmlPartials.pureMenuItemLink("/register", "Sign Up").render())

        if (UserHandler.isLoggedIn(request)) {
            if (GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "admins")) {
                model.put("dashboard_link", j2htmlPartials.pureMenuItemLink("/dashboard", "Dashboard").render())
            }
            model.put("login_or_profile_link", j2htmlPartials.pureMenuItemLink("/profile", UserHandler.loggedInUsername(request)).render())
            model.put("sign_up_menu_link", "")
            model.put("sign_out_form", j2htmlPartials.pureMenuItemForm(request.session(), "sign_out_form", "/logout", "post", "Logout").render())
        }
        return model
    }

    fun post_createPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for CREATE_PAGE page")
        var model = HashMap<String, Any>()
        model.put("template", "/templates/create_page.vtl")
        model.put("title", "Thames Valley Furs - Create page")
        model = loadNavBar(request, response, model)
        return ModelAndView(model, layoutTemplate)
    }

    fun post_register(request: Request, response: Response, layoutTemplate: String): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for REGISTER page")

        if (Web.getFormHash(request.session(), "register_form") == request.queryParams("hashid")) {
            val fullName = request.queryParams("full_name")
            val username = request.queryParams("username")
            val password = request.queryParams("password")
            val repeatedPassword = request.queryParams("repeat_password")
            val email = request.queryParams("email")

            val fullNameInputIsValid = Validation.matchFullNamePattern(fullName)
            val usernameInputIsValid = Validation.matchUsernamePattern(username)
            val passwordInputIsValid = Validation.matchPasswordPattern(password)
            val repeatedPasswordIsValid = Validation.matchPasswordPattern(repeatedPassword)
            val emailIsValid = Validation.matchEmailPattern(email)
            
            val session = request.session()

            session.attribute("user_created_successfully", false)

            if (!fullNameInputIsValid) session.attribute("full_name_field_error", true) else session.attribute("full_name_field_error", false)
            if (!usernameInputIsValid) session.attribute("username_field_error", true) else session.attribute("username_field_error", false)
            if (!passwordInputIsValid) session.attribute("password_field_error", true) else session.attribute("password_field_error", false)
            if (!repeatedPasswordIsValid) session.attribute("repeated_password_field_error", true) else session.attribute("repeated_password_field_error", false)
            if (!emailIsValid) session.attribute("email_field_error", true) else session.attribute("email_field_error", false)

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
                val user = User(fullName, username, password, email, 0, 0)
                session.attribute("user_created_successfully", UserHandler.createUser(user))
            }

        } else {
            logger.warn("${UserHandler.getSessionIdentifier(request)} -> has submitted an invalid register form...")
        }
        response.redirect("/register")
        return response
    }

    fun get_register(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for REGISTER page")

        var model = HashMap<String, Any>()
        model = loadNavBar(request, response, model)

        model.put("template", "/templates/register.vtl")
        model.put("title", "Thames Valley Furs - Sign Up")
        model.put("register_form", j2htmlPartials.pureFormAligned_Register(request.session(), "register_form", "/register", "post").render())

        return ModelAndView(model, layoutTemplate)
    }

    fun get_robotstxt(request: Request): String {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for ROBOTS.txt page")
        return pre().attr("style", "word-wrap: break-word; white-space: pre-wrap;").withText(
                "User-agent: *\n"
                +"Disallow: /dashboard/*"
        ).render()
    }

    fun get_accessDeniedPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model = loadNavBar(request, response, model)
        model.put("title", "Thames Valley Furs - Dashboard (access denied)")
        model.put("template", "/templates/access_denied.vtl")
        return ModelAndView(model, layoutTemplate)
    }

    fun get_userNotFound(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model = loadNavBar(request, response, model)
        model.put("title", "Thames Valley Furs - Profile (User not found)")
        model.put("template", "/templates/user_not_found.vtl")
        return ModelAndView(model, layoutTemplate)
    }

    fun mapFormToHash(session: Session, formTitle: String): String {
        val hash = genRandomHash()
        session.attribute(formTitle, hash)
        return hash
    }

    fun getFormHash(session: Session, formTitle: String): String = session.attribute(formTitle)
    fun genRandomHash(): String = BigInteger(130, SecureRandom()).toString(32)

}