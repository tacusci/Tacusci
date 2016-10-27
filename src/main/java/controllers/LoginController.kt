package controllers

import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*

/**
 * Created by alewis on 27/10/2016.
 */

object LoginController {

    fun get_login(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        Web.initSessionAttributes(request.session())
        val model = HashMap<String, Any>()
        if (!UserHandler.isLoggedIn(request.session())) {
            model.putIfAbsent("template", "/templates/login.vtl")
            model.putIfAbsent("title", "Thames Valley Furs - Login")
            model.putIfAbsent("stylesheet", "/css/login.css")
            val loginError: Boolean = request.session().attribute("login_error")
            if (loginError) {
                model.put("login_error", "Username or password incorrect...<br>")
                request.session().attribute("login_error", false)
            } else {
                model.put("login_error", "<br>")
            }
        } else {
            response.redirect("/dashboard")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun post_postLogin(request: Request, response: Response): Response {
        Web.initSessionAttributes(request.session())
        val username = request.queryParams("username")
        val password = request.queryParams("password")
        UserHandler.login(request.session(), username, password)
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