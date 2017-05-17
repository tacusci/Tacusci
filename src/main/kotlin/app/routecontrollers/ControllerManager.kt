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

package app.routecontrollers

import app.pages.pagecontrollers.PageController
import mu.KLogging
import spark.Session
import spark.Spark
import spark.template.velocity.VelocityTemplateEngine
import utils.Config

/**
 * Created by alewis on 21/02/2017.
 */

object ControllerManager : KLogging() {

    val baseControllers = listOf(IndexController(), DashboardController(), RegisterController(), UserManagementController(), LogFileViewController(),
                                    PageManagementController(), LoginController(), ProfileController(), ResetPasswordController(), ForgottenPasswordController())
    val layoutTemplate = "/templates/layout.vtl"

    fun initSessionAttributes(session: Session) = baseControllers.forEach { it.initSessionBoolAttributes(session) }

    fun initBaseControllers() {

        PageController.mapPagesToRoutes()

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
    fun mapAccessToStaticLocalFolder() {
        Spark.externalStaticFileLocation(Config.getProperty("static_asset_folder"))
    }
}
