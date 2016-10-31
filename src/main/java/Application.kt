/**
 * Created by alewis on 24/10/2016.
 */

import controllers.DashboardController
import controllers.LoginController
import controllers.Web
import db.DAOManager
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import java.net.URL

fun main(args: Array<String>) {

    DAOManager.init("jdbc:mysql://localhost/tvf", "root", "Nkn5rTqjB9LZb2SM")
    DAOManager.open()

    val usersDAO = DAOManager.getDAO(DAOManager.TABLE.USERS)

    val layoutTemplate = "/templates/layout.vtl"

    port(1025)
    staticFiles.location("/public")
    staticFiles.expireTime(600L)

    println(usersDAO.count())

    get("/", { request, response -> Web.get_root(request, response, layoutTemplate) }, VelocityTemplateEngine())
    get("/dashboard", { request, response -> DashboardController.get_dashboard(request, response, layoutTemplate) }, VelocityTemplateEngine())
    get("/login/sign_up", { request, response -> Web.get_signUp(request, response, layoutTemplate) }, VelocityTemplateEngine())

    get("/login", { request, response -> LoginController.get_login(request, response, layoutTemplate) }, VelocityTemplateEngine())
    get("/login/profile_page", { request, response -> Web.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())

    post("/logout", { request, response -> LoginController.post_logout(request, response) })
    post("/login/post_login", { request, response -> LoginController.post_postLogin(request, response) })
    post("/create_page", {request, response -> Web.post_createPage(request, response, layoutTemplate)}, VelocityTemplateEngine())
    post("/login/post_sign_up", { request, response -> Web.post_postSignUp(request, response, layoutTemplate)}, VelocityTemplateEngine())
}