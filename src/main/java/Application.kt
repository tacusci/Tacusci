/**
 * Created by alewis on 24/10/2016.
 */

import controllers.LoginController
import controllers.Web
import spark.ModelAndView
import spark.Session
import spark.Spark.*
import spark.TemplateEngine
import spark.template.velocity.VelocityTemplateEngine
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {

    val layoutTemplate = "/templates/layout.vtl"

    port(80)
    staticFiles.location("/public")
    staticFiles.expireTime(600L)

    get("/", { request, response -> Web.get_root(request, response, layoutTemplate) }, VelocityTemplateEngine())
    get("/dashboard", { request, response -> Web.get_dashboard(request, response, layoutTemplate) }, VelocityTemplateEngine())

    get("/login", { request, response -> LoginController.get_login(request, response, layoutTemplate) }, VelocityTemplateEngine())
    get("/profile_page", { request, response -> Web.get_profilePage(request, response, layoutTemplate) }, VelocityTemplateEngine())

    post("/logout", { request, response -> LoginController.post_logout(request, response) })
    post("/post_login", { request, response -> LoginController.post_postLogin(request, response) })
    post("/create_page", {request, response -> Web.post_createPage(request, response, layoutTemplate)}, VelocityTemplateEngine())
}