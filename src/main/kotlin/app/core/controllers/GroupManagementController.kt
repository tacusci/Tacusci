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
import app.core.Web
import app.core.handlers.GroupHandler
import app.core.handlers.UserHandler
import database.models.Group
import extensions.isNullOrBlankOrEmpty
import extensions.managedRedirect
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
    override val childGetUris: MutableList<String> = mutableListOf("/:command", "/:command/:group_id")
    override val childPostUris: MutableList<String> = mutableListOf("/:command", "/:command/:group_id")
    override val templatePath: String = "/templates/group_management.vtl"
    override val pageTitleSubstring: String = "Group Management"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {}

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for GROUP_MANAGEMENT page")
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
                if (request.params(":group_id") != null) {
                    model.put("template", "/templates/edit_group.vtl")
                    Web.insertPageTitle(request, model, "$pageTitleSubstring - Edit Page")
                    val group = GroupHandler.getGroup(request.params(":group_id").toIntSafe())
                    if (group.id == -1) response.managedRedirect(request, "/dashboard/group_management")
                    model.put("groupToEdit", group)
                } else {
                    response.managedRedirect(request, "/dashboard/group_management")
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
                if (userToAdd.id > -1)
                    if (UserHandler.userExists(userToAdd)) GroupHandler.addUserToGroup(userToAdd, groupToCreate.name)
            }
            //always add the root account to every newly created group
            GroupHandler.addUserToGroup(UserHandler.getRootAdmin(), groupToCreate.name)
        }
        response.managedRedirect(request, rootUri)
        return response
    }

    private fun post_EditGroupForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST response for EDIT_GROUP_FORM")

        // get group with existing data from database
        val groupToEdit = GroupHandler.getGroup(request.queryParams("group_id").toIntSafe())

        //get the name as whatever came back in the form submission
        val newGroupName = request.queryParams("group_name")

        //first check to make sure that the group name is not blank
        if (!newGroupName.isNullOrBlankOrEmpty()) {
        //if (!groupToEdit.name.isNullOrBlankOrEmpty()) {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> Group to edit: ${groupToEdit.name} (${groupToEdit.id})")

            //update the group in the database with possibly changed name
            if (groupToEdit.name != newGroupName) {
                groupToEdit.name = newGroupName
                GroupHandler.groupDAO.updateGroup(groupToEdit)
                logger.info("${UserHandler.getSessionIdentifier(request)} -> Updated group ${groupToEdit.name} (${groupToEdit.id}) name to $newGroupName")
            }

            logger.info("${UserHandler.getSessionIdentifier(request)} -> Removing all users from group ${groupToEdit.name} (${groupToEdit.id})")
            //clear all existing users from group (this will always leave the root admin alone however)
            GroupHandler.removeAllUsersFromGroup(groupToEdit.name)

            logger.info("${UserHandler.getSessionIdentifier(request)} -> For each user in the form's group member list, add user to group")
            //for each user in the group member list from the form submission
            request.queryParams("group_members_list").split(",").forEach {
                //get existing data for user to add to group
                val userToAdd = UserHandler.userDAO.getUser(it)
                if (userToAdd.id > -1) {
                    //if existing user found and not already in the group
                    if (UserHandler.userExists(userToAdd) && !GroupHandler.userInGroup(userToAdd, groupToEdit.name)) {
                        logger.info("${UserHandler.getSessionIdentifier(request)} -> Added user ${userToAdd.username} to group ${groupToEdit.name} (${groupToEdit.id})")
                        GroupHandler.addUserToGroup(userToAdd, groupToEdit.name)
                    } else {
                        logger.info("${UserHandler.getSessionIdentifier(request)} -> Either user ${userToAdd.username} doesn't exist or already in group ${groupToEdit.name} (${groupToEdit.id})")
                    }
                } else {
                    logger.error("${UserHandler.getSessionIdentifier(request)} -> Unable to read existing user $it from database")
                }
            }
        } else {
            logger.warn("${UserHandler.getSessionIdentifier(request)} -> Group name to edit is blank, cannot continue submission process")
        }
        response.managedRedirect(request, request.uri())
        return response
    }

    private fun post_DeleteGroupForm(request: Request, response: Response): Response {
        request.queryParams("groups_to_delete_list").split(",").forEach {
            val groupToDelete = GroupHandler.groupDAO.getGroup(it.toIntSafe())
            if (!groupToDelete.defaultGroup && !groupToDelete.hidden) {
                GroupHandler.deleteGroup(groupToDelete)
            }
        }
        response.managedRedirect(request, rootUri)
        return response
    }

    override fun post(request: Request, response: Response): Response {
        if (request.uri().contains("/group_management/create")) {
            if (Web.getFormHash(request, "create_group_form") == request.queryParams("hashid")) {
                return post_CreateGroupForm(request, response)
            }
        } else if (request.uri().contains("/group_management/edit")) {
            if (Web.getFormHash(request, "edit_group_form") == request.queryParams("hashid")) {
                return post_EditGroupForm(request, response)
            }
        } else if (request.uri().contains("/group_management/delete")) {
            if (Web.getFormHash(request, "delete_group_form") == request.queryParams("hashid")) {
                return post_DeleteGroupForm(request, response)
            }
        }
        return response
    }
}