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
import j2html.TagCreator.*
import j2html.tags.Tag
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.HTMLTable
import java.util.*

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

        val userAdminForm = form().withMethod("post").withClass("pure-form").withAction("/admin/user_management").withMethod("post")

        val userListTable = HTMLTable(listOf("Username", "Banned"))
        userListTable.className = "pure-table"
        for (username in UserHandler.userDAO.getUsernames().filter { it.isNotBlank() && it.isNotEmpty() && it != UserHandler.getLoggedInUsername(request.session()) }) {
            var checkBox = input().withType("checkbox").withName(username).withValue("username_checkbox")
            val userIsCurrentlyBannedInt = if (UserHandler.userDAO.isBanned(username) > 0) "1" else "0"
            userListTable.addRow(listOf(listOf<Tag>(label(username).withName(username).withId(username)),
                                        listOf<Tag>(input().withType("hidden").withName(username).withValue("username_hidden"),
                                                input().withType("checkbox").withName(username).withValue("username_checkbox"))))

        }
        userAdminForm.with(userListTable.render())
        userAdminForm.with(input().withType("submit").withName("update_user_management").withId("update_user_management").withValue("Update"))
        return userAdminForm.render()
    }
}