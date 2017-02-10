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

import database.daos.DAOManager
import database.daos.UserDAO
import extensions.httpsRedirect
import extensions.managedRedirect
import app.handlers.UserHandler
import mu.KLogger
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.j2htmlPartials
import java.util.*

/**
 * Created by alewis on 04/11/2016.
 */
class ProfileController : Controller {
    companion object : KLogging()

    private fun genUserProfilePage(request: Request, response: Response, username: String): HashMap<String, Any> {
        var model = HashMap<String, Any>()
        model = Web.loadNavBar(request, response, model)
        model.put("template", "/templates/profile_page.vtl")
        model.put("title", "Thames Valley Furs $username")
        model.put("username", username)
        return model
    }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for PROFILE/${request.params(":username")} page")
        var model = HashMap<String, Any>()
        model = Web.loadNavBar(request, response, model)
        //the username who's profile is requested is from the end of the URL: /profile/IamAUser
        val userNameOfProfileToView = request.params(":username")
        if (userNameOfProfileToView != null && userNameOfProfileToView.isNotBlank() && userNameOfProfileToView.isNotEmpty()) {
            if (UserHandler.userDAO.userExists(userNameOfProfileToView)) {
                model = genUserProfilePage(request, response, userNameOfProfileToView)
            } else {
                return Web.get_userNotFound(request, response, layoutTemplate)
            }
        } else {
            //if they've just requested: /profile then we give them /profile->the username of the person browsing
            if (userNameOfProfileToView == null || userNameOfProfileToView.isEmpty() || userNameOfProfileToView.isBlank()) {
                if (UserHandler.isLoggedIn(request)) response.managedRedirect(request, "/profile/${UserHandler.loggedInUsername(request)}") else response.managedRedirect(request, "/")
            } else {
                return Web.get_userNotFound(request, response, layoutTemplate)
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
