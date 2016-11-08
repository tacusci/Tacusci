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
        model.put("user_table", genUserTable(request, response))
        return ModelAndView(model, layoutTemplate)
    }

    private fun genUserTable(request: Request, response: Response): String {
        val userDAO: UserDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        val stringBuilder = StringBuilder()
        stringBuilder.appendln("<table border='1' style='width:100%'>")

        stringBuilder.append("<tr>")
        stringBuilder.append("<td>User</td>")
        stringBuilder.append("<td>Banned</td>")

        userDAO.getUsernames().forEachIndexed { i, username ->
            if (username.isNotBlank() && username.isNotEmpty()) {
                stringBuilder.append("<tr>")
                stringBuilder.append("<td>")
                stringBuilder.append(username)
                stringBuilder.append("</td>")
                stringBuilder.append("<td>"+HTMLUtils.genCheckBox("banned", "banned", "Banned", false)+"</td>")
                stringBuilder.append("</tr>")
            }
        }
        return stringBuilder.toString()
    }
}