package app/*
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
 
 
 
/**
 * Created by alewis on 24/10/2016.
 */

import controllers.*
import database.daos.DAOManager
import database.daos.GroupDAO
import handlers.GroupHandler
import handlers.UserHandler
import database.models.Group
import database.models.User
import mu.KLogging
import spark.Request
import spark.Response
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import utils.Config
import javax.jws.soap.SOAPBinding


class Application {

    val port = Config.getProperty("port")
    val dbURL = Config.getProperty("db_url")
    var dbUsername = ""
    var dbPassword = ""

    companion object : KLogging()

    fun init() {

        //connect to root SQL server instance
        DAOManager.init(dbURL, dbUsername, dbPassword)
        DAOManager.connect()
        //run the set up schemas if they don't exist
        DAOManager.setup()
        DAOManager.disconnect()
        //I AM ALMOST CERTAIN I ACTUALLY NEED TO DO THIS DISCONNECT AND RE-CONNECT
        //reconnect at the requested specific schema
        DAOManager.init(dbURL+"/${Config.getProperty("schema_name")}", dbUsername, dbPassword)
        DAOManager.connect()

        GroupHandler.createGroup(Group("admins"))
        GroupHandler.createGroup(Group("members"))
        UserHandler.createDefaultUser()
        GroupHandler.addUserToGroup(UserHandler.defaultUser, "admins")

        val layoutTemplate = "/templates/layout.vtl"

        logger.info("Setting port to $port")

        var portNum = -1
        try {
            portNum = Config.getProperty("port").toInt()
        } catch (e: NumberFormatException) {
            println("Port is not a valid number. Terminating...")
            System.exit(1)
        }
        port(portNum)
        staticFiles.location("/public")
        staticFiles.expireTime(600L)

        //MAP GET ROUTES

        get("/", { request, response -> IndexController.get_indexPage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/dashboard", { request, response -> DashboardController.get_dashboard(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/login/register", { request, response -> Web.get_register(request, response, layoutTemplate) }, VelocityTemplateEngine())

        get("/admin/user_management", { request, response -> UserManagementController.get_getUserManagement(request, response, layoutTemplate) }, VelocityTemplateEngine())

        get("/login", { request, response -> LoginController.get_login(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/profile/:username", { request, response -> ProfileController.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/profile", { request, response -> ProfileController.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())

        //TODO: Change these gets to posts/returning these pages in the response
        get("/access_denied", { request, response -> Web.get_accessDeniedPage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/user_not_found", { request, response -> Web.get_userNotFound(request, response, layoutTemplate) }, VelocityTemplateEngine())

        //MAP POST ROUTES

        post("/login", { request, response -> LoginController.post_postLogin(request, response) })
        post("/logout", { request, response -> LoginController.post_logout(request, response) })
        post("/create_page", { request, response -> Web.post_createPage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        post("/login/register", { request, response -> Web.post_register(request, response, layoutTemplate) }, VelocityTemplateEngine())
        post("/admin/user_management", { request, response -> UserManagementController.post_userManagement(request, response) })


        //MAP REDIRECTS

        redirect.get("/profile/", "/profile")
        redirect.get("/login/", "/login")

        //MAP BEFORES

        before("/dashboard", { request, response ->
            if (!UserHandler.isLoggedIn(request.session())) {
                if (!UserHandler.hasAdminRights(UserHandler.getLoggedInUsername(request.session()))) {
                    logger.info("Client at ${request.ip()} is trying to access dashboard without authentication.")
                    halt(401, "Access is denied")
                }
            }
        })

        before("/create_page", { request, response ->
            if (!UserHandler.isLoggedIn(request.session())) {
                if (!UserHandler.hasAdminRights(UserHandler.getLoggedInUsername(request.session()))) {
                    logger.info("Client at ${request.ip()} is trying to access render page without authentication.")
                    halt(401, "Access is denied")
                }
            }
        })

        before("/admin/user_management", { request, response ->
            if (!UserHandler.isLoggedIn(request.session())) {
                if (!UserHandler.hasAdminRights(UserHandler.getLoggedInUsername(request.session()))) {
                    logger.info("Client at ${request.ip()} is trying to access user management page without authentication.")
                    halt(401, "Access is denied")
                }
            }
        })
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty() || args.size < 2) { println("No username/password args"); System.exit(1) }
    Config.load()
    Config.monitorPropertiesFile()
    val application = Application()
    application.dbUsername = args[0]
    application.dbPassword = args[1]
    application.init()
}