/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016 Adam Prakash Lewis
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



package app.controllers

import app.handlers.GroupHandler
import app.handlers.UserHandler
import extensions.managedRedirect
import j2html.TagCreator.*
import j2html.tags.ContainerTag
import j2html.tags.Tag
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Config
import utils.HTMLTable
import utils.Utils
import utils.j2htmlPartials
import java.util.*

/**
 * Created by alewis on 07/11/2016.
 */

class UserManagementController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/user_management"
    override val childUris: MutableList<String> = mutableListOf()
    override val templatePath: String = "/templates/user_management.vtl"
    override val pageTitleSubstring: String = "User Management"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {
        hashMapOf(Pair("user_management_changes_made", false)).forEach { key, value -> if (!session.attributes().contains(key)) session.attribute(key, value) }
    }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for USER_MANAGEMENT page")
        var model = HashMap<String, Any>()
        model.put("template", templatePath)
        model.put("title", "${Config.getProperty("page_title")} ${Config.getProperty("page_title_divider")} $pageTitleSubstring")

        val userAdminForm = genUserForm(request)
        model.put("user_admin_form", userAdminForm.render())
        model = Web.loadNavBar(request, model)
        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {

        if (Web.getFormHash(request.session(), "user_management_form") == request.queryParams("hashid")) {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for user management form")

            val currentUserUsername = UserHandler.loggedInUsername(request)

            if (GroupHandler.userInGroup(currentUserUsername, "admins") || GroupHandler.userInGroup(currentUserUsername, "moderators")) {
                var statusChangedForAnyone = false

                val usersAndBanned = getUserIsBanned(request)
                val usersAndIsModeratorState = getUserIsModerator(request)
                val usersAndIsAdminState = getUserIsAdminStateFromForm(request)

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

                usersAndIsAdminState.forEach {
                    for ((username, admin) in it) {
                        if (username == UserHandler.getRootAdmin().username) continue
                        if (username == UserHandler.loggedInUsername(request)) continue
                        if (admin && !GroupHandler.userInGroup(username, "admins")) {
                            statusChangedForAnyone = true
                            logger.info("${UserHandler.getSessionIdentifier(request)} -> has made user $username an admin")
                            GroupHandler.addUserToGroup(username, "admins")
                        } else if (!admin && GroupHandler.userInGroup(username, "admins")) {
                            statusChangedForAnyone = true
                            logger.info("${UserHandler.getSessionIdentifier(request)} -> has removed user $username's admin status")
                            GroupHandler.removeUserFromGroup(username, "admins")
                        }
                    }
                }

                usersAndIsModeratorState.forEach {
                    for ((username, moderator) in it) {
                        if (username == UserHandler.getRootAdmin().username) continue
                        if (username == UserHandler.loggedInUsername(request)) continue
                        if (moderator && !GroupHandler.userInGroup(username, "moderators")) {
                            statusChangedForAnyone = true
                            logger.info("${UserHandler.getSessionIdentifier(request)} -> has made user $username a moderator")
                            GroupHandler.addUserToGroup(username, "moderators")
                        } else if (!moderator && GroupHandler.userInGroup(username, "moderators")) {
                            statusChangedForAnyone = true
                            logger.info("${UserHandler.getSessionIdentifier(request)} -> has removed user $username's moderator status")
                            GroupHandler.removeUserFromGroup(username, "moderators")
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

    private fun getUserIsModerator(request: Request): MutableList<MutableMap<String, Boolean>> {
        val usersAndModeratorStates = mutableListOf<MutableMap<String, Boolean>>()
        val userAndModeratorState = mutableMapOf<String, Boolean>()

        if (request.queryParams().contains("moderator_checkbox.hidden")) {
            request.queryParamsValues("moderator_checkbox.hidden").forEach { user -> userAndModeratorState.put(user, false) }
        }

        if (request.queryParams().contains("moderator_checkbox")) {
            request.queryParamsValues("moderator_checkbox").forEach { user -> userAndModeratorState.put(user, true) }
        }
        usersAndModeratorStates.add(userAndModeratorState)
        return usersAndModeratorStates
    }

    private fun getUserIsAdminStateFromForm(request: Request): MutableList<MutableMap<String, Boolean>> {
        val usersAndAdminState = mutableListOf<MutableMap<String, Boolean>>()
        val userAndAdminState = mutableMapOf<String, Boolean>()

        if (request.queryParams().contains("admin_checkbox.hidden")) {
            request.queryParamsValues("admin_checkbox.hidden").forEach { user -> userAndAdminState.put(user, false) }
        }

        if (request.queryParams().contains("admin_checkbox")) {
            request.queryParamsValues("admin_checkbox").forEach { user -> userAndAdminState.put(user, true) }
        }
        usersAndAdminState.add(userAndAdminState)
        return usersAndAdminState
    }

    private fun genUserFormForAdmin(request: Request): ContainerTag {
        //match this form instance with a random ID in the server side session
        val hash = Web.mapFormToHash(request.session(), "user_management_form")
        val userManagementForm = form().withMethod("post").withClass("pure-form").withAction(rootUri).withMethod("post")
        userManagementForm.with(input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden)
        val userListTable = HTMLTable(listOf("Date/Time", "Full Name", "Username", "Email", "Banned", "Admin", "Moderator"))
        userListTable.className = "pure-table"

        val currentUserId = UserHandler.userDAO.getUserID(UserHandler.loggedInUsername(request))
        val currentUser = UserHandler.userDAO.getUser(currentUserId)

        UserHandler.userDAO.getUsers().filter {
            it.username != currentUser.username && it.rootAdmin <= 0
        }.forEach { user ->
            val bannedCheckbox = input().withType("checkbox").withId(user.username).withValue(user.username).withName("banned_checkbox")
            val adminCheckbox = input().withType("checkbox").withId(user.username).withValue(user.username).withName("admin_checkbox")
            val moderatorCheckbox = input().withType("checkbox").withId(user.username).withValue(user.username).withName("moderator_checkbox")

            if (UserHandler.isBanned(user.username)) {
                bannedCheckbox.attr("checked", "")
            }

            if (GroupHandler.userInGroup(user, "admins")) {
                adminCheckbox.attr("checked", "")
            }

            if (GroupHandler.userInGroup(user, "moderators")) {
                moderatorCheckbox.attr("checked", "")
            }

            userListTable.addRow(listOf (
                    listOf<Tag>(label(Utils.convertMillisToDataTime(user.createdDateTime))),
                    listOf<Tag>(label(user.fullName).withName(user.username).withId(user.username)),
                    listOf(j2htmlPartials.link("", "/profile/${user.username}", user.username)),
                    listOf(j2htmlPartials.link("", "mailto:${user.email}?Subject=''", user.email)),
                    listOf<Tag>(input().withType("hidden").withId(user.username).withValue(user.username).withName("banned_checkbox.hidden"), bannedCheckbox),
                    listOf<Tag>(input().withType("hidden").withId(user.username).withValue(user.username).withName("admin_checkbox.hidden"), adminCheckbox),
                    listOf<Tag>(input().withType("hidden").withId(user.username).withValue(user.username).withName("moderator_checkbox.hidden"), moderatorCheckbox))
            )
        }

        userManagementForm.with(userListTable.render())
        if (request.session().attribute("user_management_changes_made")) {
            userManagementForm.with(p("Changes applied..."))
            request.session().attribute("user_management_changes_made", false)
        } else {
            userManagementForm.with(br())
        }
        userManagementForm.with(input().withType("submit").withClass("pure-button pure-button-primary").withName("update_user_management").withId("update_user_management").withValue("Update"))
        return userManagementForm
    }

    private fun genUserFormForModerators(request: Request): ContainerTag {
        val hash = Web.mapFormToHash(request.session(), "user_management_form")
        val userManagementForm = form().withMethod("post").withClass("pure-form").withAction(rootUri).withMethod("post")
        userManagementForm.with(input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden)
        val userListTable = HTMLTable(listOf("Date/Time", "Full Name", "Username", "Email", "Banned", "Moderator"))
        userListTable.className = "pure-table"

        val currentUserId = UserHandler.userDAO.getUserID(UserHandler.loggedInUsername(request))
        val currentUser = UserHandler.userDAO.getUser(currentUserId)

        UserHandler.userDAO.getUsers().filter {
            it.username != currentUser.username && it.rootAdmin <= 0 && !GroupHandler.userInGroup(it, "admins")
        }.forEach { user ->
            val bannedCheckbox = input().withType("checkbox").withId(user.username).withValue(user.username).withName("banned_checkbox")
            val moderatorCheckbox = input().withType("checkbox").withId(user.username).withValue(user.username).withName("moderator_checkbox")

            if (UserHandler.isBanned(user.username)) {
                bannedCheckbox.attr("checked", "")
            }

            if (GroupHandler.userInGroup(user, "moderators")) {
                moderatorCheckbox.attr("checked", "")
            }

            userListTable.addRow(listOf(
                    listOf<Tag>(label(Utils.convertMillisToDataTime(user.createdDateTime))),
                    listOf<Tag>(label(user.fullName).withName(user.username).withId(user.username)),
                    listOf(j2htmlPartials.link("", "/profile/${user.username}", user.username)),
                    listOf(j2htmlPartials.link("", "mailto:${user.email}?Subject=''", user.email)),
                    listOf<Tag>(input().withType("hidden").withId(user.username).withValue(user.username).withName("banned_checkbox.hidden"), bannedCheckbox),
                    listOf<Tag>(input().withType("hidden").withId(user.username).withValue(user.username).withName("moderator_checkbox.hidden"), moderatorCheckbox))
            )
        }

        userManagementForm.with(userListTable.render())
        if (request.session().attribute("user_management_changes_made")) {
            userManagementForm.with(p("Changes applied..."))
            request.session().attribute("user_management_changes_made", false)
        } else {
            userManagementForm.with(br())
        }
        userManagementForm.with(input().withType("submit").withClass("pure-button pure-button-primary").withName("update_user_management").withId("update_user_management").withValue("Update"))
        return userManagementForm
    }

    private fun genUserForm(request: Request): ContainerTag {
        if (GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "admins")) return genUserFormForAdmin(request)
        if (GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "moderators")) return genUserFormForModerators(request)
        return h2("Access denied")
    }
}