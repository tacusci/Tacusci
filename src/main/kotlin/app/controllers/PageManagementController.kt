package app.controllers

import app.handlers.RouteElementHandler
import app.handlers.UserHandler
import database.models.RouteElement
import database.models.RouteElementNode
import database.models.RouteElementTree
import j2html.TagCreator.li
import j2html.TagCreator.ul
import j2html.tags.ContainerTag
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.tree.Node
import java.util.*


/**
 * Created by alewis on 06/02/2017.
 */
class PageManagementController : Controller {

    override fun initSessionAttributes(session: Session) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
        val events = RouteElementNode(RouteElement(-1, root.nodeData.id, "events", RouteElementHandler.ROUTE_ELEMENT.PATH, -1))
        val reading = RouteElementNode(RouteElement(-1, events.nodeData.id, "reading", RouteElementHandler.ROUTE_ELEMENT.PATH, -1))
        val oxford = RouteElementNode(RouteElement(-1, events.nodeData.id, "oxford", RouteElementHandler.ROUTE_ELEMENT.PATH, -1))
        val oxfordChild = RouteElementNode(RouteElement(-1, oxford.nodeData.id, "I should come under oxford", RouteElementHandler.ROUTE_ELEMENT.PAGE, -1))
        val readingChild = RouteElementNode(RouteElement(-1, reading.nodeData.id, "I should come under reading", RouteElementHandler.ROUTE_ELEMENT.PAGE, -1))
        oxford.addChild(oxfordChild)
        reading.addChild(readingChild)
        events.addChild(reading)
        events.addChild(oxford)
        root.addChild(events)
        val routesAndPagesTree = RouteElementTree(root)

        model.put("tree", createRouteTree(routesAndPagesTree).render())

        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createRouteTree(routeElementTree: RouteElementTree): ContainerTag {
        val rootTag = ul()
        val innerTag = li(routeElementTree.rootElement.nodeData.name)
        if (routeElementTree.rootElement.hasChildren()) {
            addChild(innerTag, routeElementTree.rootElement.children)
        }
        return rootTag.with(innerTag)
    }

    private fun addChild(rootTagz: ContainerTag, routeElementNode: MutableList<Node<RouteElement>>): ContainerTag {
        routeElementNode.forEach { node ->
            val rootTag = ul()
            val innerTag = li(node.nodeData.name)
            if (node.hasChildren()) {
                addChild(innerTag, node.children)
            }
            rootTagz.with(rootTag.with(innerTag))
        }
        return rootTagz
    }
}