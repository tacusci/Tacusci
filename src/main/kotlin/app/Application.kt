package app

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


/**
 * Created by alewis on 24/10/2016.
 */

import controllers.*
import database.DBPoller
import database.daos.DAOManager
import database.models.Group
import handlers.GroupHandler
import handlers.UserHandler
import mu.KLogging
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import utils.Config
import java.util.*
import kotlin.concurrent.thread


class Application {

    val dbProperties = Properties()

    companion object : KLogging()

    fun connectToDB() {
        val dbURL = Config.getProperty("db_url")
        //connect to root SQL server instance
        DAOManager.init(dbURL, dbProperties)
        DAOManager.connect()
        //run the set up schemas if they don't exist
        DAOManager.setup()
        DAOManager.disconnect()
        //I AM ALMOST CERTAIN I ACTUALLY NEED TO DO THIS DISCONNECT AND RE-CONNECT
        //reconnect at the requested specific schema
        DAOManager.init(dbURL + "/${Config.getProperty("schema_name")}", dbProperties)
    }

    fun setupDefaultGroups() {
        GroupHandler.createGroup(Group("admins"))
        GroupHandler.createGroup(Group("members"))
        UserHandler.createDefaultUser()
        GroupHandler.addUserToGroup(UserHandler.defaultUser, "admins")
    }

    fun setupSpark() {
        val port = Config.getProperty("port")
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
    }

    fun setupSparkRoutes() {
        val layoutTemplate = "/templates/layout.vtl"

        //MAP GET ROUTES

        get("/", { request, response -> IndexController.get_indexPage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/dashboard", { request, response -> DashboardController.get_dashboard(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/register", { request, response -> Web.get_register(request, response, layoutTemplate) }, VelocityTemplateEngine())

        get("/dashboard/user_management", { request, response -> UserManagementController.get_getUserManagement(request, response, layoutTemplate) }, VelocityTemplateEngine())

        get("/login", { request, response -> LoginController.get_login(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/profile/:username", { request, response -> ProfileController.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/profile", { request, response -> ProfileController.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())

        //MAP POST ROUTES

        post("/login", { request, response -> LoginController.post_postLogin(request, response) })
        post("/logout", { request, response -> LoginController.post_logout(request, response) })
        post("/dashboard/create_page", { request, response -> Web.post_createPage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        post("/register", { request, response -> Web.post_register(request, response, layoutTemplate) })
        post("/dashboard/user_management", { request, response -> UserManagementController.post_userManagement(request, response) })


        //MAP REDIRECTS

        redirect.get("/profile/", "/profile")
        redirect.get("/login/", "/login")
        redirect.get("/dashboard/create_page/", "/dashboard/create_page")

        //MAP BEFORES

        before("/dashboard", { request, response ->
            if (!GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "admins")) {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> Is trying to access dashboard without authentication.")
                halt(401, "Access is denied")
            }
        })

        before("/dashboard/*", { request, response ->
            if (!GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "admins")) {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> Is trying to access dashboard sub page without authentication.")
                halt(401, "Access is denied")
            }
        })
        /*
        before("/login", { request, response ->
            if (request.ip() == "0:0:0:0:0:0:0:1") { UserHandler.login(request.session(), "tvf_admin", "Password1234!") }
        })
        */
    }

    fun restartSpark() {
        stop()
        init()
    }

    fun init() {
        connectToDB()
        setupDefaultGroups()
        setupSpark()
        setupSparkRoutes()
    }

    fun infoLog(message: String) {
        logger.info(message)
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty() || args.size < 2) {
        println("No username/password args"); System.exit(1)
    }
    Config.load()
    val application = Application()
    application.dbProperties.setProperty("user", args[0])
    application.dbProperties.setProperty("password", args[1])
    application.dbProperties.setProperty("useSSL", "false")
    application.dbProperties.setProperty("autoReconnect", "false")
    application.init()
    //Config.monitorPropertiesFile(application)

    Runtime.getRuntime().addShutdownHook(thread(name = "Shutdown thread", start = false) {
        application.infoLog("Force shut down detected, stopping everything cleanly...")
        stop()
    })
}