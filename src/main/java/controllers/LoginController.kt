package controllers

import db.DAOManager

import db.UsersDAO
import mu.KLoggable
import mu.KLogging
import mu.NamedKLogging
import spark.ModelAndView
import spark.Request
import spark.Response

import java.util.*
/**
 * Created by alewis on 27/10/2016.
 */

object LoginController: KLogging() {

    fun get_login(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info { "Recieved GET request for LOGIN page" }
        Web.initSessionAttributes(request.session())
        val model = HashMap<String, Any>()
        if (!UserHandler.isLoggedIn(request.session())) {
            logger.info { "Current user session is not logged in, giving default login page" }
            model.put("template", "/templates/login.vtl")
            model.put("title", "Thames Valley Furs - Login")
            model.put("base_stylesheet", "/css/tvf.css")
            model.put("stylesheet", "/css/ui_elements.css")
            val loginError: Boolean = request.session().attribute("login_error")
            if (loginError) {
                logger.info { "Detected previous login attempt error, altering page to include error message" }
                model.put("login_error", "Username or password incorrect...<br>")
                request.session().attribute("login_error", false)
            } else {
                model.put("login_error", "<br>")
            }
        } else {
            logger.info { "User already logged in, redirecting to landing page" }
            response.redirect("/dashboard")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun post_postLogin(request: Request, response: Response): Response {
        logger.info { "Recieved POST submission for LOGIN page" }
        Web.initSessionAttributes(request.session())
        var username = request.queryParams("username")
        var email = ""
        val password = request.queryParams("password")

        if (!(username.isNullOrBlank() || username.isNullOrEmpty() || password.isNullOrBlank() || password.isNullOrEmpty())) {
            if (username.contains("@")) {
                logger.info { "Email instead of username detected, fetching associated username" }
                email = username
                val usersDAO: UsersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UsersDAO
                username = usersDAO.getUsernameFromEmail(email)
            }
            UserHandler.login(request.session(), username, password)
        } else {
            request.session().attribute("login_error", true)
            logger.info { "Unrecognised username/password provided in form" }
        }
        logger.info { "Redirecting to login page" }
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