package controllers

import db.DAOManager
import db.UserDAO
import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*

/**
 * Created by alewis on 04/11/2016.
 */
object ProfileController {

    fun get_getUserProfilePage(username: String): HashMap<String, Any> {
        val model = HashMap<String, Any>()
        model.put("template", "/templates/profile_page.vtl")
        model.put("title", "Thames Valley Furs $username")
        model.put("username", username)
        model.put("stylesheet", "/css/ui_elements.css")
        model.put("base_stylesheet", "/css/tvf.css")
        return model
    }

    fun get_profilePage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            val userNameOfprofileToView = request.params(":username")
            val userDAO: UserDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
            if (userDAO.userExists(userNameOfprofileToView)) {
                model = get_getUserProfilePage(userNameOfprofileToView)
            } else {
                response.redirect("/user_not_found")
            }
        } else {
            response.redirect("/login")
        }
        return ModelAndView(model, layoutTemplate)
    }
}