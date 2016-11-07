/**
 * Created by alewis on 24/10/2016.
 */

import controllers.DashboardController
import controllers.LoginController
import controllers.ProfileController
import controllers.Web
import db.DAOManager
import mu.KLogging
import org.slf4j.LoggerFactory
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import java.sql.SQLException
import java.util.*
import java.util.logging.Logger


class Application {

    //TODO: Find and rename all instances of varibles named 'usersDAO' to 'userDAO'

    val port = 1025
    val dbURL = "jdbc:mysql://localhost/tvf"
    val dbUsername = "tvf_admin"
    val dbPassword = "6V2-YFu@E8"

    companion object: KLogging()

    fun init() {

        DAOManager.init(dbURL, dbUsername, dbPassword)
        logger.info("Trying to connect to DB at ${DAOManager.url}")

        try {
            DAOManager.open()
            logger.info("Connected to DB at ${DAOManager.url}")
        } catch (e: SQLException){
            logger.error("Unable to connect to db at ${DAOManager.url}... Terminating...")
            System.exit(1)
        }

        val usersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS)

        val layoutTemplate = "/templates/layout.vtl"

        logger.info("Setting port to $port")
        port(port)
        staticFiles.location("/public")
        staticFiles.expireTime(600L)

        //SET UP GET ROUTES

        get("/", { request, response -> Web.get_root(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/dashboard", { request, response -> DashboardController.get_dashboard(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/login/register", { request, response -> Web.get_register(request, response, layoutTemplate) }, VelocityTemplateEngine())

        get("/login", { request, response -> LoginController.get_login(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/login/", { request, response -> response.redirect("/login") })
        get("/profile/:username", { request, response -> ProfileController.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/profile", { request, response -> ProfileController.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/profile/", { request, response -> response.redirect("/profile") })

        get("/access_denied", { request, response -> Web.get_accessDeniedPage(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/user_not_found", { request, response -> Web.get_userNotFound(request, response, layoutTemplate) }, VelocityTemplateEngine())

        //SET UP POST ROUTES

        post("/login", { request, response -> LoginController.post_postLogin(request, response) })
        post("/logout", { request, response -> LoginController.post_logout(request, response) })
        post("/create_page", {request, response -> Web.post_createPage(request, response, layoutTemplate)}, VelocityTemplateEngine())
        post("/login/register", { request, response -> Web.post_register(request, response, layoutTemplate)}, VelocityTemplateEngine())

        before("/dashboard", { request, response ->
            if (redirectToLoginIfNotAuthenticated(request, response)) {
                logger.info { "Client at ${request.ip()} is trying to access dashboard without authentication. Redirecting to login page" }
            }
        })

        before("/create_page", { request, response ->
            if (showAccessDeniedIfNotAuthenticated(request, response)) {
                logger.info { "Client at ${request.ip()} is trying to access create page without authentication. Redirecting to login page" }
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
    val application = Application()
    application.init()
}