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
 
 
 
 package controllers

import handlers.UserHandler
import j2html.TagCreator.*
import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*

/**
 * Created by tauraamui on 15/12/2016.
 */
object IndexController {

    fun get_indexPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        Web.initSessionAttributes(request.session())
        val model = HashMap<String, Any>()
        model.put("template", "/templates/index.vtl")
        model.put("title", "Thames Valley Furs - Homepage")
        if (UserHandler.isLoggedIn(request.session())) {
            val userProfileLink = a().withHref("/profile").withClass("pure-button").withText(UserHandler.getLoggedInUsername(request.session()))
            model.put("profile_or_login_link", userProfileLink.render())
            model.put("sign_up_link", "")
        } else {
            model.put("profile_or_login_link", a().withHref("/login").withText("Login").render())
            model.put("sign_up_link", a().withHref("/login/register").withText("Sign Up").render())
        }
        println(model)
        return ModelAndView(model, layoutTemplate)
    }
}