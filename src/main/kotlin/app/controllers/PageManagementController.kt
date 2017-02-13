package app.controllers

import app.handlers.RouteElementHandler
import app.handlers.UserHandler
import database.models.RouteElement
import j2html.TagCreator.li
import j2html.TagCreator.ul
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.tree.Node
import utils.tree.Tree
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
        model.put("page_menu", "/templates/page_menu.vtl")
        model = Web.loadNavBar(request, response, model)

        val eventsPages = Node<RouteElement>()
        eventsPages.data = RouteElement("events", RouteElementHandler.ROUTE_ELEMENT.PATH)
        val oxfordBowlplexPageNode = Node<RouteElement>()
        oxfordBowlplexPageNode.data = RouteElement("oxford_bowlplex", RouteElementHandler.ROUTE_ELEMENT.PAGE)
        val readingFursPageNode = Node<RouteElement>()
        readingFursPageNode.data = RouteElement("reading_furs", RouteElementHandler.ROUTE_ELEMENT.PAGE)
        eventsPages.addChild(readingFursPageNode)
        eventsPages.addChild(oxfordBowlplexPageNode)

        val routesAndPages = Tree<RouteElement>()
        routesAndPages.rootElement = eventsPages

        println(routesAndPages)

        val pageTree = ul().with(li("Pages").with(ul()
                .with(li("/").with(ul().with(
                        li("events"),
                        li("about_us")
                )))
        ))

        model.put("tree", pageTree.render())

        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}