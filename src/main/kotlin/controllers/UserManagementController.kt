package controllers

import db.DAOManager
import db.UserDAO
import htmlutils.HTMLUtils
import htmlutils.HTMLTable
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

        val stringBuilder = StringBuilder()

        val userListTable = HTMLTable(listOf("Username", "Banned"))

        val userDAO: UserDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO

        for (username in userDAO.getUsernames().filter { it.isNotBlank() && it.isNotEmpty() }) {
            val userIsCurrentlyBannedBool = if (userDAO.getUserBanned(username) > 0) true else false
            userListTable.addRow(listOf(username, HTMLUtils.genCheckBox("banned", "Banned", userIsCurrentlyBannedBool)))
        }

        stringBuilder.append(userListTable.create())

        return userListTable.create()
    }
}