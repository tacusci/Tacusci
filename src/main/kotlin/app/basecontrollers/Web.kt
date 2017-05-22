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
 
 
 
 package app.basecontrollers

import app.handlers.GroupHandler
import app.handlers.UserHandler
import j2html.TagCreator.h2
import j2html.TagCreator.pre
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import spark.template.velocity.VelocityIMTemplateEngine
import utils.Config
import utils.Utils
import utils.j2htmlPartials
import api.users.TacusciAPI
import java.io.File
import java.util.*

/**
 * Created by alewis on 25/10/2016.
 */

//TODO need to rename this class
object Web : KLogging() {

    fun loadNavBar(request: Request, model: HashMap<String, Any>): HashMap<String, Any> {
        model.put("home_link", j2htmlPartials.pureMenuItemLink("/", "Home").render())
        model.put("login_or_profile_link", j2htmlPartials.pureMenuItemLink("/login", "Login").render())
        model.put("sign_up_menu_link", j2htmlPartials.pureMenuItemLink("/register", "Sign Up").render())

        if (UserHandler.isLoggedIn(request)) {
            val username = UserHandler.loggedInUsername(request)
            if (GroupHandler.userInGroup(username, "admins") || GroupHandler.userInGroup(username, "moderators")) {
                model.put("dashboard_link", j2htmlPartials.pureMenuItemLink("/dashboard", "Dashboard").render())
            }
            model.put("login_or_profile_link", j2htmlPartials.pureMenuItemLink("/profile", UserHandler.loggedInUsername(request)).render())
            model.put("sign_up_menu_link", "")
            model.put("sign_out_form", j2htmlPartials.pureMenuItemForm(request.session(), "sign_out_form", "/login", "post", "Logout").render())
        }
        return model
    }

    fun get_robotstxt(request: Request): String {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for ROBOTS.txt page")
        val robotsFile = File(Config.getProperty("robots_file"))
        if (robotsFile.exists()) {
            return pre().attr("style", "word-wrap: break-word; white-space: pre-wrap;").withText(
                    robotsFile.readText()
            ).render()
        } else {
            return pre().attr("style", "word-wrap: break-word; white-space: pre-wrap;").withText(
                    "User-agent: *\n"
                            +"Disallow: /dashboard/*"
            ).render()
        }
    }

    fun get_userNotFound(request: Request, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model = loadNavBar(request, model)
        model.put("title", "${Config.getProperty("page_title")} ${Config.getProperty("page_title_divider")} Profile (User not found)")
        model.put("template", "/templates/404_not_found.vtl")
        model = TacusciAPI.injectAPIInstances(request, model)
        return ModelAndView(model, layoutTemplate)
    }

    fun gen_accessDeniedPage(request: Request, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model.put("title", "${Config.getProperty("page_title")} ${Config.getProperty("page_title_divider")} Access Denied")
        model.put("template", "/templates/access_denied.vtl")
        model = TacusciAPI.injectAPIInstances(request, model)
        return ModelAndView(model, layoutTemplate)
    }

    fun get404Page(request: Request): String {
        val responsePagesFolder = File("${Config.getProperty("static_asset_folder")}/${Config.getProperty("response_pages_folder")}")
        var fourOhFourFile = File("")
        listOf("404.html", "404.md", "404.vtl").forEach {
            val currentFile = File(responsePagesFolder.absolutePath+"/$it")
            if (currentFile.exists()) fourOhFourFile = currentFile; return@forEach
        }
        val velocityIMTemplateEngine = VelocityIMTemplateEngine()
        velocityIMTemplateEngine.insertTemplateAsString("fourOhFourTemplate", (if (fourOhFourFile.exists()) fourOhFourFile.readText() else h2("404").render()))
        TacusciAPI.injectAPIInstances(request, "fourOhFourTemplate", velocityIMTemplateEngine)
        val result = velocityIMTemplateEngine.render("fourOhFourTemplate")
        velocityIMTemplateEngine.flush("fourOhFourTemplate")
        return result
    }

    fun get500Page(request: Request): String {
        val responsePagesFolder = File("${Config.getProperty("static_asset_folder")}/${Config.getProperty("response_pages_folder")}")
        var fiveHundredOhFiveFile = File("")
        listOf("500.html", "500.md", "500.vtl").forEach {
            val currentFile = File(responsePagesFolder.absolutePath+"/$it")
            if (currentFile.exists()) fiveHundredOhFiveFile = currentFile; return@forEach
        }
        val velocityIMTemplateEngine = VelocityIMTemplateEngine()
        velocityIMTemplateEngine.insertTemplateAsString("fiveHundredOhFive", (if (fiveHundredOhFiveFile.exists()) fiveHundredOhFiveFile.readText() else h2("500").render()))
        TacusciAPI.injectAPIInstances(request, "fiveHundredOhFive", velocityIMTemplateEngine)
        val result = velocityIMTemplateEngine.render("fiveHundredOhFive")
        velocityIMTemplateEngine.flush("fiveHundredOhFive")
        return result
    }

    fun mapFormToHash(session: Session, formTitle: String): String {
        val hash = Utils.randomHash(80)
        session.attribute(formTitle, hash)
        return hash
    }

    fun getFormHash(session: Session, formTitle: String): String = session.attribute(formTitle)
}