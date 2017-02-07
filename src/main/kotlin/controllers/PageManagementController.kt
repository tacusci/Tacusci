package controllers

import handlers.UserHandler
import j2html.TagCreator.li
import j2html.TagCreator.ul
import spark.ModelAndView
import spark.Request
import spark.Response
import java.util.*

/**
 * Created by alewis on 06/02/2017.
 */
class PageManagementController : Controller {

    /*

    The premise for page management is to have a branch for every subpage

     */

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        DashboardController.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for PAGE_MANAGEMENT page")
        var model = HashMap<String, Any>()
        model.put("template", "/templates/page_management.vtl")
        model.put("title", "Thames Valley Furs - Page Management")
        model = Web.loadNavBar(request, response, model)

        model.put("pages_list", genPagesList())

        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun genPagesList(): String {
        return ul().with(
                li("/events").with(
                        ul().with(
                                li("London Furs"),
                                li("Oxford")
                        )
                ),
                li("/info").with(
                        ul().with(
                                li("About Us"),
                                li("Contact Us")
                        )
                )
        ).render()
    }
}