/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

package app.core.controllers

import api.core.TacusciAPI
import app.core.core.controllers.Web
import app.core.core.handlers.GroupHandler
import app.core.handlers.UserHandler
import database.models.Group
import extensions.isNullOrBlankOrEmpty
import extensions.toIntSafe
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import java.util.*

/**
 * Created by alewis on 29/06/2017.
 */

class GroupManagementController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/group_management"
    override val childUris: MutableList<String> = mutableListOf("/:command", "/:command/:group_id")
    override val templatePath: String = "/templates/group_management.vtl"
    override val pageTitleSubstring: String = "Group Management"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {}

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        UserManagementController.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for GROUP_MANAGEMENT page")
        val model = HashMap<String, Any>()
        model.put("template", templatePath)

        TacusciAPI.injectAPIInstances(request, response, model)
        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavigationElements(request, model)

        if (request.params(":command") == null && request.params(":group_id") == null) {
            model.put("template", templatePath)
        } else {
            return getCommandPage(request, response, layoutTemplate)
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun getCommandPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        TacusciAPI.injectAPIInstances(request, response, model)
        Web.loadNavigationElements(request, model)
        when (request.params(":command")) {
            "create" -> {
                model.put("template", "/templates/create_group.vtl")
                Web.insertPageTitle(request, model, "$pageTitleSubstring - Create Group")
                model.put("groupToCreate", Group())
            }
            
            "edit" -> {
                if (request.params("group_id") != null) {
                    model.put("template", "/templates/edit_group.vtl")
                    Web.insertPageTitle(request, model, "$pageTitleSubstring - Edit Page")
                    val group = GroupHandler.groupDAO.getGroup(request.params("group_id").toIntSafe())
                    if (group.id == -1) response.redirect("/dashboard/group_management")
                    model.put("groupToEdit", group)
                } else {
                    response.redirect("/dashboard/group_management")
                }
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun post_CreateGroupForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST response for CREATE_GROUP_FORM")
        val groupToCreate = Group()
        groupToCreate.name = request.queryParams("group_name")
        if (!groupToCreate.name.isNullOrBlankOrEmpty()) {
            GroupHandler.createGroup(groupToCreate)
            request.queryParams("group_members_list").split(",").forEach {
                val userToAdd = UserHandler.userDAO.getUser(it)
                if (userToAdd.id > -1) {
                    GroupHandler.addUserToGroup(userToAdd, groupToCreate.name)
                }
            }
        }
        return response
    }

    override fun post(request: Request, response: Response): Response {
        if (request.uri().contains("/group_management/create")) {
            if (Web.getFormHash(request, "create_group_form") == request.queryParams("hashid")) {
                return post_CreateGroupForm(request, response)
            }
        }
        return response
    }
}