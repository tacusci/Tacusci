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
import database.models.User
import extensions.managedRedirect
import extensions.toIntSafe
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import java.util.*

/**
 * Created by alewis on 07/11/2016.
 */

class UserManagementController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/user_management"
    override val childUris: MutableList<String> = mutableListOf("/:command", "/:command/:user_id")
    override val templatePath: String = "/templates/user_management.vtl"
    override val pageTitleSubstring: String = "User Management"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {
        hashMapOf(Pair("user_management_changes_made", false)).forEach { key, value -> if (!session.attributes().contains(key)) session.attribute(key, value) }
    }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for USER_MANAGEMENT page")
        val model = HashMap<String, Any>()
        model.put("template", templatePath)

        TacusciAPI.injectAPIInstances(request, response, model)
        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavigationElements(request, model)

        model.put("user_management_changes_made", request.session().attribute("user_management_changes_made"))

        if (request.params(":command") == null && request.params(":page_id") == null) {
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
                model.put("template", "/templates/create_user.vtl")
                Web.insertPageTitle(request, model, "$pageTitleSubstring - Create User")
                model.put("userToCreate", User())
            } "edit" -> {
                if (request.params("user_id") != null) {
                    model.put("template", "/templates/edit_user.vtl")
                    Web.insertPageTitle(request, model, "$pageTitleSubstring - Edit User")
                    val user = UserHandler.getUserById(request.params("user_id").toIntSafe())
                    if (user.id == -1) response.redirect(rootUri)
                    model.put("userToEdit", user)
                } else {
                    response.redirect(rootUri)
                }
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun post_CreateUserForm(request: Request, response: Response): Response {
        val userToCreate = User()
        userToCreate.username = request.queryParams("username")
        userToCreate.fullName = request.queryParams("full_name")
        userToCreate.email = request.queryParams("email")
        userToCreate.password = request.queryParams("password")
        UserHandler.createUser(userToCreate)
        response.redirect(request.uri())
        return response
    }

    private fun post_DeleteUserForm(request: Request, response: Response): Response {
        val userToDelete = UserHandler.getUserById(request.queryParams("user_ids").toIntSafe())
        if (!userToDelete.rootAdmin && userToDelete.id != UserHandler.userDAO.getUser(UserHandler.loggedInUsername(request)).id) {
            UserHandler.userDAO.deleteUser(userToDelete)
        }
        response.redirect(rootUri)
        return response
    }

    override fun post(request: Request, response: Response): Response {

        if (request.uri().contains("user_management/create")) {
            if (Web.getFormHash(request, "create_user_form") == request.queryParams("hashid")) {
                return post_CreateUserForm(request, response)
            }
        } else if (request.uri().contains("user_management/delete")) {
            if (Web.getFormHash(request, "delete_user_form") == request.queryParams("hashid")) {
                return post_DeleteUserForm(request, response)
            }
        }

        if (request.uri() == rootUri) {
            if (Web.getFormHash(request, "user_management_form") == request.queryParams("hashid")) {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for user management form")

                val currentUserUsername = UserHandler.loggedInUsername(request)

                if (GroupHandler.userInGroup(currentUserUsername, "admins") || GroupHandler.userInGroup(currentUserUsername, "moderators")) {
                    var statusChangedForAnyone = false

                    val usersAndBanned = getUserIsBanned(request)

                    usersAndBanned.forEach {
                        for ((username, banned) in it) {
                            if (username == UserHandler.getRootAdmin().username) continue
                            if (username == UserHandler.loggedInUsername(request)) continue
                            if (banned && !UserHandler.isBanned(username)) {
                                statusChangedForAnyone = true
                                logger.info("${UserHandler.getSessionIdentifier(request)} -> has banned user $username")
                                UserHandler.ban(username)
                            } else if (!banned && UserHandler.isBanned(username)) {
                                statusChangedForAnyone = true
                                logger.info("${UserHandler.getSessionIdentifier(request)} -> has unbanned user $username")
                                UserHandler.unban(username)
                            }
                        }
                    }
                    if (statusChangedForAnyone) request.session().attribute("user_management_changes_made", true) else request.session().attribute("user_management_changes_made", false)
                } else {
                    logger.warn("${UserHandler.getSessionIdentifier(request)} -> has no right to submit user management form...")
                }
            } else {
                logger.warn("${UserHandler.getSessionIdentifier(request)} -> Has submitted an invalid user management form...")
            }
        }

        response.managedRedirect(request, rootUri)
        return response
    }

    private fun getUserIsBanned(request: Request): MutableList<MutableMap<String, Boolean>> {
        val usersAndBanStates = mutableListOf<MutableMap<String, Boolean>>()
        val userAndBanState = mutableMapOf<String, Boolean>()

        if (request.queryParams().contains("banned_checkbox.hidden")) {
            request.queryParamsValues("banned_checkbox.hidden").forEach { user -> userAndBanState.put(user, false) }
        }

        if (request.queryParams().contains("banned_checkbox")) {
            request.queryParamsValues("banned_checkbox").forEach { user -> userAndBanState.put(user, true) }
        }

        usersAndBanStates.add(userAndBanState)
        return usersAndBanStates
    }
}