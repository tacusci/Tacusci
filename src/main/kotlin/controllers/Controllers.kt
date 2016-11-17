package controllers

import db.models.*
import spark.*
import java.util.*

/**
 * Created by alewis on 25/10/2016.
 */

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
        model.put("template", "/templates/index.vtl")
        model.put("title", "Thames Valley Furs - Homepage")
        return ModelAndView(model, layoutTemplate)
    }

    fun post_createPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("template", "/templates/create_page.vtl")
        model.put("title", "Thames Valley Furs - Create page")
        return ModelAndView(model, layoutTemplate)
    }

    fun get_register(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("template", "/templates/register.vtl")
        model.put("title", "Thames Valley Furs - Sign Up")
        model.put("full_name_error_hidden", "hidden")
        model.put("username_error_hidden", "hidden")
        model.put("password_error_hidden", "hidden")
        model.put("email_error_hidden", "hidden")

        if (!request.session().attributes().contains("full_name_field_error")) { request.session().attribute("full_name_field_error", false) }
        if (!request.session().attributes().contains("username_field_error")) { request.session().attribute("username_field_error", false) }
        if (!request.session().attributes().contains("password_field_error")) { request.session().attribute("password_field_error", false) }
        if (!request.session().attributes().contains("email_field_error")) { request.session().attribute("email_field_error", false) }

        if (request.session().attribute("full_name_field_error")) {
            model.put("full_name_error_hidden", "")
            request.session().removeAttribute("full_name_field_error")
        }

        if (request.session().attribute("username_field_error")) {
            model.put("username_error_hidden", "")
            request.session().removeAttribute("username_field_error")
        }

        if (request.session().attribute("password_field_error")) {
            model.put("password_error_hidden", "")
            request.session().removeAttribute("password_field_error")
        }

        if (request.session().attribute("email_field_error")) {
            model.put("email_error_hidden", "")
            request.session().removeAttribute("email_field_error")
        }

        return ModelAndView(model, layoutTemplate)
    }

    fun post_register(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        val fullName = request.queryParams("full_name")
        val username = request.queryParams("username")
        val password = request.queryParams("password")
        val email = request.queryParams("email")

        model.put("full_name_error_hidden", true)

        val user = NewUser(fullName, username, password, email, 0)
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
            response.redirect("/login/sign_up")
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