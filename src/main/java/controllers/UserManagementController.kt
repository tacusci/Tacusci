package controllers

import db.DAOManager
import db.UserDAO
import htmlutils.HTMLUtils
import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*

/**
 * Created by alewis on 07/11/2016.
 */

object UserManagementController {

    fun get_getUserManagement(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("template", "/templates/user_management.vtl")
        model.put("title", "Thames Valley Furs - User Management")
        model.put("stylesheet", "/css/ui_elements.css")
        model.put("base_stylesheet", "/css/tvf.css")
        model.put("user_list", genUserList(request, response))
        return ModelAndView(model, layoutTemplate)
    }

    private fun genUserList(request: Request, response: Response): String {

        val userDAO: UserDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        val stringBuilder = StringBuilder()
        for (username in userDAO.getUsernames()) {
            stringBuilder.appendln("<label>$username</label>")
            stringBuilder.appendln(HTMLUtils.genRadioButton("banned", "banned", "Banned"))
            stringBuilder.appendln("<br>")
        }

        return stringBuilder.toString()
    }
}