package controllers

import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Route
import java.util.*

/**
 * Created by alewis on 25/10/2016.
 */

fun redirectToLoginIfNotLoggedIn(request: Request, response: Response) {
    if (!UserHandler.isLoggedIn(request.session())) response.redirect("/")
}

object Web {

    fun get_root(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            response.redirect("/dashboard")
        } else {
            model.putIfAbsent("template", "/templates/index.vtl")
            model.putIfAbsent("title", "Thames Valley Furs - Homepage")
            val linkList = listOf("/login", "/dashboard", "profile_page")
            model.putIfAbsent("pagelist", Gen.generateList(linkList).toString())
            model.putIfAbsent("stylesheet", "/css/tvf.css")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun get_profilePage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            val username: String = request.session().attribute("username")
            model.putIfAbsent("template", "/templates/profile_page.vtl")
            model.putIfAbsent("title", "Thames Valley Furs $username")
            model.putIfAbsent("username", username)
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
            model.putIfAbsent("title", "Thames Valley Furs - Login")
            val loginError: Boolean = request.session().attribute("loginerror")
            if (loginError) { model.putIfAbsent("") }
            model.putIfAbsent("stylesheet", "/css/login.css")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun get_dashboard(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        redirectToLoginIfNotLoggedIn(request, response)
        if (UserHandler.isInGroup(request.session().attribute("username"), "administrators")) {
            model.putIfAbsent("template", "/templates/dashboard.vtl")
            model.putIfAbsent("title", "Thames Valley Furs - Dashboard")
            model.putIfAbsent("username", request.session().attribute("username"))
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun post_createPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        redirectToLoginIfNotLoggedIn(request, response)
        model.putIfAbsent("template", "/templates/create_page.vtl")
        model.putIfAbsent("title", "Thames Valley Furs - Create page")
        return ModelAndView(model, layoutTemplate)
    }
}

private object Gen {

    fun generateList(list: List<Any>): StringBuilder {
        val stringBuilder = StringBuilder()
        stringBuilder.append("<ul>")
        list.forEach { element ->
            stringBuilder.append("<li>$element</li>")
        }
        stringBuilder.append("</ul><ul>")
        return stringBuilder
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
        redirectToLoginIfNotLoggedIn(request, response)
        if (UserHandler.isLoggedIn(request.session())) {
            UserHandler.logout(request.session())
            response.redirect("/")
        }
        return response
    }
}