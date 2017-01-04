/*
# DON'T BE A DICK PUBLIC LICENSE

> Version 1.1, December 2016

> Copyright (C) 2016 Adam Prakash Lewis
 
 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document.

> DON'T BE A DICK PUBLIC LICENSE
> TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

 1. Do whatever you like with the original work, just don't be a dick.

     Being a dick includes - but is not limited to - the following instances:

	 1a. Outright copyright infringement - Don't just copy this and change the name.  
	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.  
	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.  

 2. If you become rich through modifications, related works/services, or supporting the original work,
 share the love. Only a dick would make loads off this work and not buy the original work's 
 creator(s) a pint.
 
 3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes 
 you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */
 
 
 
 package controllers

import handlers.UserHandler
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*
import j2html.TagCreator.*
/**
 * Created by alewis on 07/11/2016.
 */

object UserManagementController: KLogging() {

    fun get_getUserManagement(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        model.put("template", "/templates/user_management.vtl")
        model.put("title", "Thames Valley Furs - User Management")
        model.put("user_admin_form", genUserForm(request, response))
        return ModelAndView(model, layoutTemplate)
    }

    fun post_userManagement(request: Request, response: Response) {
        logger.info("Recieved post submission for user management page")
        Web.initSessionAttributes(request.session())
        println(request.body())
        //TODO: REMEMBER TO PREVENT THE CURRENTLY LOGGED IN USER FROM BEING ABLE TO BAN THEMSELVES...
    }

    private fun genUserForm(request: Request, response: Response): String {

        val userAdminForm = HTMLForm()
        userAdminForm.className = "pure-form"
        userAdminForm.action = "/admin/user_management"
        userAdminForm.method = "post"

        val userListTable = HTMLTable(listOf("Username", "Banned"))
        userListTable.className = "pure-table"
        for (username in UserHandler.userDAO.getUsernames().filter { it.isNotBlank() && it.isNotEmpty() && it != UserHandler.getLoggedInUsername(request.session()) }) {
            val userIsCurrentlyBannedBool = if (UserHandler.userDAO.getUserBanned(username) > 0) true else false
            userListTable.addRow(listOf(HTMLUtils.genLabel(content = username, id = username),
                    HTMLUtils.genInput(type = "hidden", name = username, value = "0")+HTMLUtils.genCheckBox(username, username, userIsCurrentlyBannedBool)))
        }
        userAdminForm.content.add(userListTable.create())
        userAdminForm.content.add(HTMLUtils.genInput(type = "submit", name = "update_user_management", id = "update_user_management", value = "Update"))
        return userAdminForm.create()
    }
}