package controllers

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
        return ModelAndView(model, layoutTemplate)
    }
}