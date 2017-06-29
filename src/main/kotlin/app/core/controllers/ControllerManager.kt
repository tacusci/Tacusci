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

import app.core.core.controllers.*
import app.core.core.handlers.GroupHandler
import app.core.handlers.UserHandler
import database.daos.DAOManager
import database.daos.RoutePermissionDAO
import database.models.RoutePermission
import mu.KLogging
import spark.Session
import spark.Spark
import spark.template.velocity.VelocityTemplateEngine
import utils.Config

/**
 * Created by alewis on 21/02/2017.
 */

object ControllerManager : KLogging() {

    val baseControllers = listOf(DashboardController(), RegisterController(), UserManagementController(),
                                    LogFileViewController(), PageManagementController(), TemplateManagementController(),
                                        LoginController(), ProfileController(), ResetPasswordController(),
                                            ForgottenPasswordController(), GroupManagementController())
    val layoutTemplate = "/templates/layout.vtl"

    fun initSessionAttributes(session: Session) = baseControllers.forEach { it.initSessionBoolAttributes(session) }

    fun initBaseControllers() {

        baseControllers.forEach {
            logger.debug("Mapping route: ${it.rootUri}")
            if (it.handlesGets) Spark.get(it.rootUri, { request, response -> it.get(request, response, layoutTemplate) }, VelocityTemplateEngine())
            if (it.handlesPosts) Spark.post(it.rootUri, { request, response -> it.post(request, response) })

            it.childUris.forEach { childUri ->
                logger.debug("Mapping route: ${it.rootUri+childUri}")
                if (it.handlesGets) Spark.get(it.rootUri+childUri, { request, response -> it.get(request, response, layoutTemplate) }, VelocityTemplateEngine())
                if (it.handlesPosts) Spark.post(it.rootUri+childUri, { request, response -> it.post(request, response) })
            }
        }
    }

    fun initDefaultRoutePermissions() {
        val routePermissionDAO = DAOManager.getDAO(DAOManager.TABLE.ROUTE_PERMISSIONS) as RoutePermissionDAO
        val dashboardAccessGroupId = GroupHandler.groupDAO.getGroupID("dashboard_access")
        routePermissionDAO.insertRoutePermission(RoutePermission(title = "Dashboard Access", route = "/dashboard", groupId = dashboardAccessGroupId))
        routePermissionDAO.insertRoutePermission(RoutePermission(title = "Dashboard Access Wildcard", route = "/dashboard/*", groupId = dashboardAccessGroupId))
    }

    fun mapAccessToStaticLocalFolder() {
        Spark.externalStaticFileLocation(Config.getProperty("static-asset-folder"))
    }

    fun applyGroupPermissionsToRoutes() {

        val routePermissionDAO = DAOManager.getDAO(DAOManager.TABLE.ROUTE_PERMISSIONS) as RoutePermissionDAO

        routePermissionDAO.getRoutePermissions().forEach { routePermission ->
            Spark.before(routePermission.route, { request, response ->
                if (UserHandler.isLoggedIn(request)) {
                    val group = GroupHandler.groupDAO.getGroup(routePermission.groupId)
                    if (!GroupHandler.userInGroup(UserHandler.loggedInUsername(request), group.name)) {
                        logger.info("${UserHandler.getSessionIdentifier(request)} -> is trying to access ${routePermission.route} but isn't a member of group ${group.name}")
                        Spark.halt(401, VelocityTemplateEngine().render(Web.gen_accessDeniedPage(request, response, layoutTemplate)))
                    }
                } else {
                    logger.info("${UserHandler.getSessionIdentifier(request)} -> is trying to access ${routePermission.route} but is not logged in...")
                    Spark.halt(401, VelocityTemplateEngine().render(Web.gen_accessDeniedPage(request, response, layoutTemplate)))
                }
            })
        }
    }

    fun initResponsePages() {
        Spark.notFound({ request, response -> Web.get404Page(request, response) })
        Spark.internalServerError({ request, response -> Web.get500Page(request, response) })
    }
}
