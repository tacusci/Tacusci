/**
 * Created by alewis on 24/10/2016.
 */

import controllers.*
import db.DAOManager
import db.UserHandler
import mu.KLogging
import spark.Request
import spark.Response
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import utils.Config


class Application {

    val port = Config.getProperty("port")
    val dbURL = Config.getProperty("db_url")
    var dbUsername = ""
    var dbPassword = ""

    companion object : KLogging()

    fun init() {

        DAOManager.init(dbURL, dbUsername, dbPassword)
        DAOManager.connect()
        DAOManager.setup()
        DAOManager.disconnect()
        DAOManager.init(dbURL+"/${Config.getProperty("schema_name")}", dbUsername, dbPassword)
        DAOManager.connect()

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

        //SET UP GET ROUTES

        get("/", { request, response -> Web.get_root(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/dashboard", { request, response -> DashboardController.get_dashboard(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/login/register", { request, response -> Web.get_register(request, response, layoutTemplate) }, VelocityTemplateEngine())

        get("/admin/user_management", { request, response -> UserManagementController.get_getUserManagement(request, response, layoutTemplate) }, VelocityTemplateEngine())

        get("/login", { request, response -> LoginController.get_login(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/login/", { request, response -> response.redirect("/login") })
        get("/profile/:username", { request, response -> ProfileController.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/profile", { request, response -> ProfileController.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/profile/", { request, response -> response.redirect("/profile") })

        //TODO: Change these gets to posts/returning these pages in the response
        get("/access_denied", { request, response -> Web.get_accessDeniedPage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/user_not_found", { request, response -> Web.get_userNotFound(request, response, layoutTemplate) }, VelocityTemplateEngine())

        //SET UP POST ROUTES

        post("/login", { request, response -> LoginController.post_postLogin(request, response) })
        post("/logout", { request, response -> LoginController.post_logout(request, response) })
        post("/create_page", { request, response -> Web.post_createPage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        post("/login/register", { request, response -> Web.post_register(request, response, layoutTemplate) }, VelocityTemplateEngine())
        post("/admin/user_management", { request, response -> UserManagementController.post_userManagement(request, response) })

        before("/dashboard", { request, response ->
            if (redirectToLoginIfNotAuthenticated(request, response)) {
                logger.info("Client at ${request.ip()} is trying to access dashboard without authentication. Redirecting to login page")
            }
        })

        before("/create_page", { request, response ->
            if (showAccessDeniedIfNotAuthenticated(request, response)) {
                logger.info("Client at ${request.ip()} is trying to access create page without authentication. Showing access denied page")
            }
        })

        before("/admin/user_management", { request, response ->
            if (showAccessDeniedIfNotAuthenticated(request, response)) {
                logger.info("Client at ${request.ip()} is trying to access user management page without authentication. Showing access denied page")
            }
        })
    }

    fun redirectToLoginIfNotAuthenticated(request: Request, response: Response): Boolean {
        if (!UserHandler.isLoggedIn(request.session())) {
            response.redirect("/login")
            halt()
            return true
        }
        return false
    }

    fun showAccessDeniedIfNotAuthenticated(request: Request, response: Response): Boolean {
        if (!UserHandler.isLoggedIn(request.session())) {
            response.redirect("/access_denied")
            halt()
            return true
        }
        return false
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