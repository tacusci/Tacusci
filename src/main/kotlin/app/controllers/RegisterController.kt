package app.controllers

import database.models.User
import extensions.managedRedirect
import app.handlers.UserHandler
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.Validation
import utils.j2htmlPartials
import java.util.*

/**
 * Created by alewis on 02/02/2017.
 */

class RegisterController : Controller {

    companion object : KLogging()

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        Web.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for REGISTER page")

        var model = HashMap<String, Any>()
        model = Web.loadNavBar(request, response, model)

        model.put("template", "/templates/register.vtl")
        model.put("title", "Thames Valley Furs - Sign Up")
        model.put("register_form", j2htmlPartials.pureFormAligned_Register(request.session(), "register_form", "/register", "post").render())

        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        Web.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for REGISTER page")

        if (Web.getFormHash(request.session(), "register_form") == request.queryParams("hashid")) {
            val fullName = request.queryParams("full_name")
            val username = request.queryParams("username")
            val password = request.queryParams("password")
            val repeatedPassword = request.queryParams("repeat_password")
            val email = request.queryParams("email")

            request.session().attribute("user_created_successfully", false)

            val fullNameInputIsValid = Validation.matchFullNamePattern(fullName)
            val usernameInputIsValid = Validation.matchUsernamePattern(username)
            val passwordInputIsValid = Validation.matchPasswordPattern(password)
            val repeatedPasswordIsValid = Validation.matchPasswordPattern(repeatedPassword)
            val emailIsValid = Validation.matchEmailPattern(email)

            if (!fullNameInputIsValid) request.session().attribute("full_name_field_error", true) else request.session().attribute("full_name_field_error", false)
            if (!usernameInputIsValid) request.session().attribute("username_field_error", true) else request.session().attribute("username_field_error", false)
            if (!passwordInputIsValid) request.session().attribute("password_field_error", true) else request.session().attribute("password_field_error", false)
            if (!repeatedPasswordIsValid) request.session().attribute("repeated_password_field_error", true) else request.session().attribute("repeated_password_field_error", false)
            if (!emailIsValid) request.session().attribute("email_field_error", true) else request.session().attribute("email_field_error", false)

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
                val user = User(-1, fullName, username, password, email, 0, 0)
                request.session().attribute("user_created_successfully", true)
                UserHandler.createUser(user)
            }
        } else {
            Web.logger.warn("${UserHandler.getSessionIdentifier(request)} -> has submitted an invalid register form...")
        }
        response.managedRedirect(request, "/register")
        return response
    }
}