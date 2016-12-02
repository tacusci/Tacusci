package controllers

import db.DAOManager
import db.UserDAO
import htmlutils.HTMLForm
import htmlutils.HTMLUtils
import htmlutils.HTMLTable
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*

/**
 * Created by alewis on 07/11/2016.
 */

object UserManagementController: KLogging() {

    fun get_getUserManagement(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("template", "/templates/user_management.vtl")
        model.put("title", "Thames Valley Furs - User Management")
        model.put("user_table", genUserTable(request, response))
        return ModelAndView(model, layoutTemplate)
    }

    fun post_userManagement(request: Request, response: Response) {
        logger.info("Recieved post submission for user management page")
        Web.initSessionAttributes(request.session())
        //trying to figure out best way to associate checkbox to table row
        request.queryParams().forEach { param ->
            request.queryParams(param).forEach(::println)
        }
    }

    private fun genUserTable(request: Request, response: Response): String {
        val userAdminForm = HTMLForm()
        userAdminForm.className = "pure-form"
        userAdminForm.action = "/admin/user_management"
        userAdminForm.method = "post"

        val userListTable = HTMLTable(listOf("Username", "Banned"))
        userListTable.className = "pure-table"
        val userDAO: UserDAO = DAOManager.getDAO(DAOManager.TABLE.USERS) as UserDAO
        for (username in userDAO.getUsernames().filter { it.isNotBlank() && it.isNotEmpty() }) {
            val userIsCurrentlyBannedBool = if (userDAO.getUserBanned(username) > 0) true else false
            userListTable.addRow(listOf(username, HTMLUtils.genCheckBox("banned", "$username", userIsCurrentlyBannedBool)))
        }

        userAdminForm.content = userListTable.create()

        return userListTable.create()
    }
}