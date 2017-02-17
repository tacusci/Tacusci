package app.controllers

import app.handlers.RouteElementHandler
import app.handlers.UserHandler
import database.models.Page
import database.models.RouteElement
import database.models.RouteElementNode
import database.models.RouteElementTree
import j2html.TagCreator.li
import j2html.TagCreator.ul
import j2html.tags.ContainerTag
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
        model.put("page_menu", "/templates/page_menu.vtl")
        model = Web.loadNavBar(request, response, model)

        val root = RouteElementNode(RouteElement(-1, -1, "Pages", RouteElementHandler.ROUTE_ELEMENT.PATH, -1))
        val eventsPages = RouteElementNode(RouteElement(-1, root.nodeData.id, "events", RouteElementHandler.ROUTE_ELEMENT.PATH, -1))
        eventsPages.addChild(RouteElementNode(RouteElement(-1, eventsPages.nodeData.parentId, "reading_furs", RouteElementHandler.ROUTE_ELEMENT.PAGE, 0)))
        val thames = Page(1, "Thames", "Some content again")
        val reading = RouteElementNode(RouteElement(-1, eventsPages.nodeData.parentId, "oxford_bowlplex", RouteElementHandler.ROUTE_ELEMENT.PAGE, -1))
        reading.addChild(RouteElementNode(RouteElement(-1, reading.nodeData.parentId, "sub child", RouteElementHandler.ROUTE_ELEMENT.PAGE, -1)))
        eventsPages.addChild(reading)
        root.addChild(eventsPages)
        val routesAndPagesTree = RouteElementTree(root)

        val pageTree = ul().with(li("Pages").with(ul().with(li("events").with(ul().with(li("reading_furs"), li("oxford_bowlplex")
                )))
        ))

        model.put("tree", pageTree.render())

        println(createRouteTree(routesAndPagesTree))

        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createRouteTree(routeElementTree: RouteElementTree): ContainerTag {
        return ul().with(li(routeElementTree.rootElement.nodeData.name).with(
                ul().with(routeElementTree.rootElement.children.map { child ->
                    ul().with(li(child.nodeData.name).with(ul().with(child.children.map { child2 ->
                        li(child2.nodeData.name)
                    })))
                })
        ))
    }
}