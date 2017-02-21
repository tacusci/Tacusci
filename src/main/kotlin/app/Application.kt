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

import app.controllers.ControllerManager
import app.controllers.Web
import app.handlers.GroupHandler
import app.handlers.UserHandler
import database.daos.DAOManager
import database.models.Group
import extensions.managedRedirect
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
        UserHandler.createRootAdmin()
        //root admin might already exist but check for properties file changes
        UserHandler.updateRootAdmin()
    }

    fun setupSpark() {
        var portNum = -1
        try {
            portNum = Config.getProperty("port").toInt()
            logger.info("Setting port to $portNum")
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

        ControllerManager.routesAndControllers.forEach {
            get(it.key, { request, response -> it.value.get(request, response, layoutTemplate) }, VelocityTemplateEngine())
            post(it.key, { request, response -> it.value.post(request, response) })
        }

        get("/robots.txt", { request, response -> Web.get_robotstxt(request) })

        //MAP BEFORES

        before { request, response ->
            if (request.uri() != "/" && request.uri().endsWith("/")) {
                response.managedRedirect(request, request.uri().removeSuffix("/"))
            }

            val session = request.session()

            ControllerManager.routesAndControllers.forEach { it.value.initSessionAttributes(session) }

            //TODO: Need to move these to their respective app.controllers
            val sessionAttributes = hashMapOf(
                    Pair("user_management_changes_made", false), Pair("lines_to_show", "20"), Pair("text_to_show", ""),
                    Pair("full_name_field_error", false), Pair("username_field_error", false), Pair("password_field_error", false),
                    Pair("repeated_password_field_error", false), Pair("email_field_error", false), Pair("username_not_available_error", false),
                    Pair("username_not_available", ""), Pair("user_created_successfully", false), Pair("passwords_mismatch_error", false))
            sessionAttributes.forEach { pair -> if (!session.attributes().contains(pair.key)) session.attribute(pair.key, pair.value) }
            session.maxInactiveInterval(20*60)
        }

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