/**
 * Created by alewis on 24/10/2016.
 */

import controllers.DashboardController
import controllers.LoginController
import controllers.Web
import db.DAOManager
import mu.KLogging
import org.slf4j.LoggerFactory
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import java.sql.SQLException
import java.util.logging.Logger


class Application {

    val port = 1025
    val dbURL = "jdbc:mysql://localhost/tvf"
    val dbUsername = "root"
    val dbPassword = "Nkn5rTqjB9LZb2SM"

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

        get("/", { request, response -> Web.get_root(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/dashboard", { request, response -> DashboardController.get_dashboard(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/login/sign_up", { request, response -> Web.get_signUp(request, response, layoutTemplate) }, VelocityTemplateEngine())

        get("/login", { request, response -> LoginController.get_login(request, response, layoutTemplate) }, VelocityTemplateEngine())
        get("/login/", { request, response -> response.redirect("/login") })
        get("/login/profile_page", { request, response -> Web.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())

        post("/logout", { request, response -> LoginController.post_logout(request, response) })
        post("/login/post_login", { request, response -> LoginController.post_postLogin(request, response) })
        post("/create_page", {request, response -> Web.post_createPage(request, response, layoutTemplate)}, VelocityTemplateEngine())
        post("/login/post_sign_up", { request, response -> Web.post_postSignUp(request, response, layoutTemplate)}, VelocityTemplateEngine())
    }
}


fun main(args: Array<String>) {
    val application = Application()
    application.init()
}