package controllers

import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*

/**
 * Created by tauraamui on 27/10/2016.
 */

object DashboardController {

    fun get_dashboard(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        Web.initSessionAttributes(request.session())
        val model = HashMap<String, Any>()
        val username: String = request.session().attribute("username")
        model.put("template", "/templates/dashboard.vtl")
        model.put("base_stylesheet", "/css/tvf.css")
        model.put("title", "Thames Valley Furs - Dashboard")
        model.put("username", request.session().attribute("username"))
        return ModelAndView(model, layoutTemplate)
    }
}
