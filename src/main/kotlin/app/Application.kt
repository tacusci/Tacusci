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

import api.core.TacusciAPI
import app.core.controllers.ControllerManager
import app.core.Web
import app.core.handlers.GroupHandler
import app.core.handlers.UserHandler
import app.core.pages.pagecontrollers.PageController
import database.daos.DAOManager
import database.daos.TacusciInfoDAO
import database.models.Group
import extensions.managedRedirect
import extensions.toIntSafe
import mu.KLogging
import spark.Spark.*
import utils.CliOption
import utils.CliOptions
import utils.Config
import java.util.*
import kotlin.concurrent.thread


class Application {

    var restarting = false
    val dbProperties = Properties()
    val layoutTemplate = "/templates/layout.vtl"

    companion object : KLogging()

    fun setupDatabase() {
        val dbURL = Config.getProperty("db-url")
        //connect to root SQL server instance
        DAOManager.init(dbURL, dbProperties)
        DAOManager.connect()
        //run the set up schemas if they don't exist
        DAOManager.setup()
        DAOManager.disconnect()

        if (DAOManager.dbProperties.getProperty("server-type") == "MYSQL") {
            //I AM ALMOST CERTAIN I ACTUALLY NEED TO DO THIS DISCONNECT AND RE-CONNECT
            //reconnect at the requested specific schema
            DAOManager.init(dbURL + "/${Config.getProperty("schema-name")}", dbProperties)
        } else {
            DAOManager.init(dbURL, dbProperties)
        }
    }

    fun updateDatabase() {
        val tacusciVersionDAO = DAOManager.getDAO(DAOManager.TABLE.TACUSCI_INFO) as TacusciInfoDAO
        val tacusciVersionFromDB = tacusciVersionDAO.getTacusciInfo()

        val tacusciVersionFromDBNumber = Integer.parseInt(tacusciVersionFromDB.versionNumberMajor.toString() + tacusciVersionFromDB.versionNumberMinor.toString() + tacusciVersionFromDB.versionNumberRevision.toString())
        val tacusciVersionNumber = Integer.parseInt(Config.getProperty("tacusci-version-major") + Config.getProperty("tacusci-version-minor") + Config.getProperty("tacusci-version-revision"))

        if (tacusciVersionFromDBNumber < tacusciVersionNumber) {

        }
    }

    fun setupDefaultGroups() {
        GroupHandler.createGroup(Group(name = "dashboard_access", defaultGroup = true, hidden = true))
        val dashboardGroupId = GroupHandler.groupDAO.getGroupID("dashboard_access")
        GroupHandler.createGroup(Group(name = "admins", parentGroupId = dashboardGroupId, defaultGroup = true))
        GroupHandler.createGroup(Group(name = "moderators", parentGroupId = dashboardGroupId, defaultGroup = true))
        GroupHandler.createGroup(Group(name = "members", defaultGroup = true))
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
        val maxThreads = Config.getProperty("max-threads").toIntSafe()
        val minThreads = Config.getProperty("min-threads").toIntSafe()
        val threadIdleTimeout = Config.getProperty("thread-idle-timeout").toIntSafe()

        if (maxThreads > 0) threadPool(maxThreads)
        if (maxThreads > 0 && minThreads > 0 && threadIdleTimeout > 0) threadPool(maxThreads, minThreads, threadIdleTimeout)

        //this is to map the public folder within the .jar package, where all the default CSS, JS etc., is
        staticFiles.location("/public")
        staticFiles.expireTime(600L)
        setupSparkRoutes()
    }

    fun setupSparkRoutes() {

        ControllerManager.mapAccessToStaticLocalFolder()
        ControllerManager.initDefaultRoutePermissions()
        ControllerManager.initBaseControllers()

        PageController.setupPages()

        get("/robots.txt", { request, response -> Web.get_robotstxt(request) })

        //MAP BEFORES

        before( "*", { request, response ->
            if (request.uri() != "/" && request.uri().endsWith("/")) {
                response.managedRedirect(request, request.uri().removeSuffix("/"))
            }
            val session = request.session()
            ControllerManager.initSessionAttributes(session)
            session.maxInactiveInterval(Config.getProperty("session-idle-timeout").toIntSafe())
        })

        ControllerManager.applyGroupPermissionsToRoutes()
        ControllerManager.initResponsePages()
    }

    /*
    fun restartTacusci() {
        println("Restarting Tacusci")
        val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
        val currentJar = File(Application.javaClass.protectionDomain.codeSource.location.toURI())

        if (!currentJar.name.endsWith(".jar"))
            return

        val command = mutableListOf<String>()
        command.add(javaBin)
        command.add("-jar")
        command.add(currentJar.path)
        CliOptions.cliOptions.forEach { cliOption ->
            if (!cliOption.value.isNullOrBlankOrEmpty()) {
                command.add("--{cliOption.cliText}")
                if (cliOption.argumentExpected) {
                    command.add(cliOption.value)
                }
            }
        }
        val processBuilder = ProcessBuilder(command)
        processBuilder.start()
        System.exit(0)
    }
    */

    fun restartServer() {
        logger.info("Restarting...")
        restarting = true
        thread {
            Thread.sleep(300)
            init()
        }
        stop()
        DAOManager.disconnect()
        restarting = false
    }

    fun init() {
        setupDatabase()
        updateDatabase()
        setupDefaultGroups()
        Web.init()
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
            CliOption("Disable verbose output in debug mode", "disable-debug-output", false)))
    CliOptions.parseArgs(args)

    Config.load()

    val application = Application()

    Runtime.getRuntime().addShutdownHook(thread(name = "Shutdown thread", start = false) {
        if (!application.restarting) {
            application.infoLog("Force shut down detected, stopping everything cleanly...")
            stop()
        }
    })

    Config.setProperty("tacusci-version-major", "1")
    Config.setProperty("tacusci-version-minor", "3")
    Config.setProperty("tacusci-version-revision", "5")

    application.dbProperties.setProperty("user", CliOptions.getOptionValue("username"))
    application.dbProperties.setProperty("password", CliOptions.getOptionValue("password"))
    application.dbProperties.setProperty("useSSL", "false")
    application.dbProperties.setProperty("autoReconnect", "false")
    TacusciAPI.setApplication(application)
    application.init()
    //Config.monitorPropertiesFile(application)
}