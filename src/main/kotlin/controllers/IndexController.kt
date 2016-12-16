package controllers

import db.UserHandler
import htmlutils.HTMLUtils
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
        if (UserHandler.isLoggedIn(request.session())) {
            val currentSessionUsername = UserHandler.getLoggedInUsername(request.session())
            model.put("profile_or_login_link", HTMLUtils.genLink("/dashboard", currentSessionUsername))
        } else {
            model.put("profile_or_login_link", HTMLUtils.genLink("/login", "Login"))
        }
        model.put("title", "Thames Valley Furs - Homepage")
        return ModelAndView(model, layoutTemplate)
    }
}