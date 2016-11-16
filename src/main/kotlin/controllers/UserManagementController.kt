package controllers

import db.DAOManager
import db.UserDAO
import htmlutils.HTMLForm
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
        model.put("user_table", genUserTable(request, response))
        return ModelAndView(model, layoutTemplate)
    }

    private fun genUserTable(request: Request, response: Response): String {

        val userAdminForm = HTMLForm()
        userAdminForm.className = "pure-form"

        val userListTable = HTMLTable(listOf("Username", "Banned"))
        userListTable.className = "pure-table"
        val userDAO: UserDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        for (username in userDAO.getUsernames().filter { it.isNotBlank() && it.isNotEmpty() }) {
            val userIsCurrentlyBannedBool = if (userDAO.getUserBanned(username) > 0) true else false
            userListTable.addRow(listOf(username, HTMLUtils.genCheckBox("banned", "Banned", userIsCurrentlyBannedBool)))
        }

        userAdminForm.content = userListTable.create()

        return userAdminForm.create()
    }
}