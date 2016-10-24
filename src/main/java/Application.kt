/**
 * Created by alewis on 24/10/2016.
 */

import spark.ModelAndView
import spark.Spark.*
import spark.template.velocity.VelocityTemplateEngine
import java.util.*

fun main(args: Array<String>) {

    port(80)
    staticFileLocation("/public")

    val layout = "templates/layout.vtl"

    get("/", { request, response ->
        val model = HashMap<String, Any>()
        model.putIfAbsent("username", request.session().attribute("username"))
        model.putIfAbsent("template", "/templates/welcome.vtl")
        ModelAndView(model, layout)
    }, VelocityTemplateEngine())

    post("/welcome", { request, response ->
        val model = HashMap<String, Any>()
        val inputtedUsername = request.queryParams("username")
        val inputtedPassword = request.queryParams("password")
        if (inputtedUsername == "tauraamui" && inputtedPassword == "Password1234!") {
            request.session().attribute("logged_in", true)
            request.session().attribute("username", inputtedUsername)
        }
        request.session().attribute("username", inputtedUsername)
        model.putIfAbsent("username", inputtedUsername)
        model.putIfAbsent("template", "templates/welcome.vtl")
        ModelAndView(model, layout)
    }, VelocityTemplateEngine())

    post("/logout", { request, response ->
        if (request.session().attribute("logged_in")) {
            request.session().attribute("username", null)
            request.session().attribute("logged_in", false)
        }
        response.redirect("/")
    })
}