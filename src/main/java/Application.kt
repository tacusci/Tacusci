/**
 * Created by alewis on 24/10/2016.
 */

import spark.ModelAndView
import spark.Session
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {

    val layoutTemplate = "/templates/layout.vtl"

    port(80)
    staticFiles.location("/public")
    staticFiles.expireTime(600L)

    get("/", { request, response ->
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) response.redirect("/profile_page")
        model.putIfAbsent("template", "/templates/index.vtl")
        ModelAndView(model, layoutTemplate)
    }, VelocityTemplateEngine())

    get("/profile_page", { request, response ->
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) {
            model.putIfAbsent("template", "/templates/profile_page.vtl")
            model.putIfAbsent("username", request.session().attribute("username"))
        } else {
            response.redirect("/login")
        }
        ModelAndView(model, layoutTemplate)
    }, VelocityTemplateEngine())

    get("/login", { request, response ->
        val model = HashMap<String, Any>()
        if (UserHandler.isLoggedIn(request.session())) response.redirect("/")
        model.putIfAbsent("template", "/templates/login.vtl")
        ModelAndView(model, layoutTemplate)
    }, VelocityTemplateEngine())

    post("/post_login", { request, response ->
        println(request.queryParams("username"))
        println(request.queryParams("password"))
        val username = request.queryParams("username")
        val password = request.queryParams("password")
        UserHandler.login(request.session(), username, password)
        if (UserHandler.isLoggedIn(request.session())) response.redirect("/")
        response.redirect("/login")
        ModelAndView(HashMap<String, Any>(), layoutTemplate)
    }, VelocityTemplateEngine())

    post("/logout", { request, response ->
        if (UserHandler.isLoggedIn(request.session())) {
            UserHandler.logout(request.session())
        }
        response.redirect("/")
    })
}