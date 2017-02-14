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

    private fun buildRouteTree() {
        val root = RouteElementNode(RouteElement(-1, -1, "Pages", RouteElementHandler.ROUTE_ELEMENT.PATH, -1))
        val eventsPages = RouteElementNode(RouteElement(-1, -1, "events", RouteElementHandler.ROUTE_ELEMENT.PATH, -1))
        eventsPages.addChild(RouteElementNode(RouteElement(-1, -1, "reading_furs", RouteElementHandler.ROUTE_ELEMENT.PAGE, 0)))
        val readingFursPage = Page(0, "Reading Furs", "Some content")
        eventsPages.addChild(RouteElementNode(RouteElement(-1, -1, "oxford_bowlplex", RouteElementHandler.ROUTE_ELEMENT.PAGE, -1)))
        root.addChild(eventsPages)
        val routesAndPagesTree = RouteElementTree(root)

        val routeTree = ul().with(li(routesAndPagesTree.rootElement.nodeData.name).with(
                ul().with(routesAndPagesTree.toList().map { node -> nodeChildrenToLis(ul(), node as RouteElementNode) })
        ))
    }

    fun nodeChildrenToLis(rootTag: ContainerTag, routeElementNode: RouteElementNode): ContainerTag {
        //I know this makes no sense but it's 1 in the morning I'll check it tomorrow
        rootTag.with(routeElementNode.children.map { li(it.nodeData.name) })
        routeElementNode.children.forEach { nodeChildrenToLis(rootTag, it as RouteElementNode) }
        return rootTag
    }
}