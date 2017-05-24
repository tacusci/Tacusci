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

package app

/**
 * Created by alewis on 24/10/2016.
 */

import app.corecontrollers.ControllerManager
import app.corecontrollers.Web
import app.handlers.GroupHandler
import app.handlers.UserHandler
import app.pages.pagecontrollers.PageController
import database.daos.DAOManager
import database.models.Group
import extensions.managedRedirect
import extensions.toIntSafe
import mu.KLogging
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import utils.CliOption
import utils.CliOptions
import utils.Config
import java.util.*
import kotlin.concurrent.thread


class Application {

    val dbProperties = Properties()
    val layoutTemplate = "/templates/layout.vtl"

    companion object : KLogging()

    fun setupDatabase() {
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
        GroupHandler.createGroup(Group("moderators"))
        GroupHandler.createGroup(Group("members"))
        UserHandler.createRootAdmin()
        //root admin might already exist but check for properties file changes
        UserHandler.updateRootAdmin()
    }

    fun setupSpark() {
        var portNum = -1
        try {
            portNum = Config.getProperty("port").toIntSafe()
            logger.info("Setting port to $portNum")
        } catch (e: NumberFormatException) {
            logger.error("Port is not a valid number. Terminating...")
            System.exit(1)
        }
        port(portNum)

        //these config values will basically be -1 if they're not set
        val maxThreads = Config.getProperty("max_threads").toIntSafe()
        val minThreads = Config.getProperty("min_threads").toIntSafe()
        val threadIdleTimeout = Config.getProperty("thread_idle_timeout").toIntSafe()

        if (maxThreads > 0) threadPool(maxThreads)
        if (maxThreads > 0 && minThreads > 0 && threadIdleTimeout > 0) threadPool(maxThreads, minThreads, threadIdleTimeout)

        staticFiles.location("/public")
        staticFiles.expireTime(600L)
        setupSparkRoutes()
    }

    fun setupSparkRoutes() {

        ControllerManager.mapAccessToStaticLocalFolder()
        ControllerManager.initBaseControllers()

        PageController.mapPagesToRoutes()

        get("/robots.txt", { request, response -> Web.get_robotstxt(request) })

        //MAP BEFORES

        before( "*", { request, response ->
            if (request.uri() != "/" && request.uri().endsWith("/")) {
                response.managedRedirect(request, request.uri().removeSuffix("/"))
            }

            val session = request.session()
            ControllerManager.initSessionAttributes(session)
            session.maxInactiveInterval(20 * 60)
        })

        before("/dashboard", { request, response ->
            if (!GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "admins") && !GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "moderators")) {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> Is trying to access dashboard without authentication.")
                halt(401, VelocityTemplateEngine().render(Web.gen_accessDeniedPage(request, response, layoutTemplate)))
            }
        })

        before("/dashboard/*", { request, response ->
            if (!GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "admins") && !GroupHandler.userInGroup(UserHandler.loggedInUsername(request), "moderators")) {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> Is trying to access dashboard sub page without authentication.")
                halt(401, VelocityTemplateEngine().render(Web.gen_accessDeniedPage(request, response, layoutTemplate)))
            }
        })

        //MAP CUSTOM RESPONSE PAGES

        notFound({ request, response -> Web.get404Page(request, response) })
        internalServerError({ request, response -> Web.get500Page(request, response) })
    }

    fun restartServer() {
        stop()
        init()
    }

    fun init() {
        setupDatabase()
        setupDefaultGroups()
        setupSpark()
    }

    fun infoLog(message: String) {
        logger.info(message)
    }
}

fun main(args: Array<String>) {
    CliOptions.cliOptions.addAll(listOf(CliOption("Username", "username", true),
            CliOption("Password", "password", true),
            CliOption("Debug Mode", "debug", false),
            CliOption("Disable vibose output in debug mode", "disable_debug_output", false)))
    CliOptions.parseArgs(args)

    Config.load()

    val application = Application()

    Runtime.getRuntime().addShutdownHook(thread(name = "Shutdown thread", start = false) {
        application.infoLog("Force shut down detected, stopping everything cleanly...")
        stop()
    })

    application.dbProperties.setProperty("user", CliOptions.getOptionValue("username"))
    application.dbProperties.setProperty("password", CliOptions.getOptionValue("password"))
    application.dbProperties.setProperty("useSSL", "false")
    application.dbProperties.setProperty("autoReconnect", "false")
    application.init()
    //Config.monitorPropertiesFile(application)
}