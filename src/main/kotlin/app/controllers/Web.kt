/*
# DON'T BE A DICK PUBLIC LICENSE

> Version 1.1, December 2016

> Copyright (C) 2016 Adam Prakash Lewis
 
 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document.

> DON'T BE A DICK PUBLIC LICENSE
> TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

 1. Do whatever you like with the original work, just don't be a dick.

     Being a dick includes - but is not limited to - the following instances:

	 1a. Outright copyright infringement - Don't just copy this and change the name.  
	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.  
	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.  

 2. If you become rich through modifications, related works/services, or supporting the original work,
 share the love. Only a dick would make loads off this work and not buy the original work's 
 creator(s) a pint.
 
 3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes 
 you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */
 
 
 
 package app.controllers

import app.handlers.GroupHandler
import app.handlers.UserHandler
import j2html.TagCreator.pre
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Config
import utils.Utils
import utils.j2htmlPartials
import java.io.File
import java.util.*

/**
 * Created by alewis on 25/10/2016.
 */

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

    fun get_userNotFound(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model = loadNavBar(request, model)
        model.put("title", "Thames Valley Furs - Profile (User not found)")
        model.put("template", "/templates/user_not_found.vtl")
        return ModelAndView(model, layoutTemplate)
    }

    fun gen_accessDeniedPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model.put("title", "Thames Valley Furs - Access Denied")
        model.put("template", "/templates/access_denied.vtl")
        model = loadNavBar(request, model)
        model.put("access_denied_message", j2htmlPartials.centeredMessage("Access is denied", j2htmlPartials.HeaderType.h1).render())
        return ModelAndView(model, layoutTemplate)
    }

    fun mapFormToHash(session: Session, formTitle: String): String {
        val hash = Utils.randomHash()
        session.attribute(formTitle, hash)
        return hash
    }

    fun getFormHash(session: Session, formTitle: String): String = session.attribute(formTitle)
}