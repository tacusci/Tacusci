package controllers

import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Route
import java.util.*

/**
 * Created by alewis on 25/10/2016.
 */

object Web {

    fun get_root(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            response.redirect("/dashboard")
        } else {
            model.putIfAbsent("template", "/templates/index.vtl")
            model.putIfAbsent("stylesheet", "/css/tvf.css")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun get_profilePage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            model.putIfAbsent("template", "/templates/profile_page.vtl")
            model.putIfAbsent("username", request.session().attribute("username"))
            model.putIfAbsent("stylesheet", "/css/tvf.css")
        } else {
            response.redirect("/login")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun get_login(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            response.redirect("/")
        } else {
            model.putIfAbsent("template", "/templates/login.vtl")
            model.putIfAbsent("stylesheet", "/css/login.css")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun get_dashboard(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            if (UserHandler.isInGroup(request.session().attribute("username"), "administrators")) {
                model.putIfAbsent("template", "/templates/dashboard.vtl")
            }
        } else {
            response.redirect("/login")
        }
        return ModelAndView(model, layoutTemplate)
    }
}

object UserSession {

    fun post_postLogin(request: Request, response: Response): Response {
        val username = request.queryParams("username")
        val password = request.queryParams("password")
        UserHandler.login(request.session(), username, password)
        if (UserHandler.isLoggedIn(request.session())) response.redirect("/dashboard") else response.redirect("/")
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