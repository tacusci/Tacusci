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
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.j2htmlPartials
import java.util.HashMap

/**
 * Created by tauraamui on 15/12/2016.
 */
object IndexController : KLogging() {

    fun get_indexPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for INDEX page")
        Web.initSessionAttributes(request.session())
        val model = HashMap<String, Any>()
        model.put("template", "/templates/index.vtl")
        model.put("title", "Thames Valley Furs - Homepage")

        if (UserHandler.isLoggedIn(request.session())) {
            model.put("profile_or_login_link", j2htmlPartials.pureChartElement("/login", UserHandler.getLoggedInUsername(request.session())).render())
            model.put("sign_up_link", "")
        } else {
            model.put("profile_or_login_link", j2htmlPartials.pureChartElement("/login", "Login").render())
            model.put("sign_up_link", j2htmlPartials.pureChartElement("/login/register", "Sign Up").render())
        }
        return ModelAndView(model, layoutTemplate)
    }
}