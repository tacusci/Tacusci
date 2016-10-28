package controllers

import db.models.User
import db.models.isValid
import spark.*
import java.util.*

/**
 * Created by alewis on 25/10/2016.
 */

fun redirectToLoginIfNotLoggedIn(request: Request, response: Response) {
    if (!UserHandler.isLoggedIn(request.session())) response.redirect("/")
}

object Web {

    fun initSessionAttributes(session: Session) {
        if (!session.attributes().contains("login_error")) {
            session.attribute("login_error", false)
        } else if (!session.attributes().contains("username")) {
            session.attribute("username", "")
        }
    }

    fun get_root(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        initSessionAttributes(request.session())
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            response.redirect("/dashboard")
        } else {
            model.put("template", "/templates/index.vtl")
            model.put("title", "Thames Valley Furs - Homepage")
            val linkList = listOf("Login", "Dashboard", "Profile Page")
            model.put("stylesheet", "/css/ui_elements.css")
            model.put("pagelist", Gen.generateList(linkList).toString())
            model.put("base_stylesheet", "/css/tvf.css")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun get_profilePage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            val username: String = request.session().attribute("username")
            model.put("template", "/templates/profile_page.vtl")
            model.put("title", "Thames Valley Furs $username")
            model.put("username", username)
            model.put("stylesheet", "/css/ui_elements.css")
            model.put("base_stylesheet", "/css/tvf.css")
        } else {
            response.redirect("/login")
        }
        return ModelAndView(model, layoutTemplate)
    }

    fun post_createPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        redirectToLoginIfNotLoggedIn(request, response)
        model.put("template", "/templates/create_page.vtl")
        model.put("base_stylesheet", "/css/tvf.css")
        model.put("stylesheet", "/css/ui_elements.css")
        model.put("title", "Thames Valley Furs - Create page")
        return ModelAndView(model, layoutTemplate)
    }

    fun get_signUp(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("template", "/templates/sign_up.vtl")
        model.put("base_stylesheet", "/css/tvf.css")
        model.put("stylesheet", "/css/ui_elements.css")
        model.put("title", "Thames Valley Furs - Sign Up")
        return ModelAndView(model, layoutTemplate)
    }

    fun post_postSignUp(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        val fullName = request.queryParams("full_name")
        val username = request.queryParams("username")
        val password = request.queryParams("password")
        val email = request.queryParams("email")

        val user = User(fullName, username, password, email)
        if (UserHandler.createUser(user)) {
            response.redirect("/login")
        }

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