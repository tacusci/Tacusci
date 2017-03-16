package app.controllers

import app.handlers.RouteEntityHandler
import app.handlers.UserHandler
import database.models.RouteEntity
import database.models.RouteEntityNode
import database.models.RouteEntityTree
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
        //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        model = Web.loadNavBar(request, model)

        val root = RouteEntityNode(RouteEntity(-1, -1, "Pages", RouteEntityHandler.ROUTE_ENTITY_TYPE.PATH, -1))
        val events = RouteEntityNode(RouteEntity(-1, root.data.id, "events", RouteEntityHandler.ROUTE_ENTITY_TYPE.PATH, -1))
        val reading = RouteEntityNode(RouteEntity(-1, events.data.id, "reading", RouteEntityHandler.ROUTE_ENTITY_TYPE.PATH, -1))
        val oxford = RouteEntityNode(RouteEntity(-1, events.data.id, "oxford", RouteEntityHandler.ROUTE_ENTITY_TYPE.PATH, -1))
        val oxfordChild = RouteEntityNode(RouteEntity(-1, oxford.data.id, "I should come under oxford", RouteEntityHandler.ROUTE_ENTITY_TYPE.PAGE, -1))
        val readingChild = RouteEntityNode(RouteEntity(-1, reading.data.id, "I should come under reading", RouteEntityHandler.ROUTE_ENTITY_TYPE.PAGE, -1))
        oxford.addChild(oxfordChild)
        reading.addChild(readingChild)
        events.addChild(reading)
        events.addChild(oxford)
        root.addChild(events)
        val routesAndPagesTree = RouteEntityTree(root)

        model.put("tree", createRouteTree(routesAndPagesTree).render())

        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createRouteTree(routeEntityTree: RouteEntityTree): ContainerTag {
        val rootTag = ul()
        val innerTag = li(routeEntityTree.rootElement.data.name)
        if (routeEntityTree.rootElement.hasChildren()) {
            addChild(innerTag, routeEntityTree.rootElement.children)
        }
        return rootTag.with(innerTag)
    }

    private fun addChild(rootTagz: ContainerTag, routeEntityNode: MutableList<Node<RouteEntity>>): ContainerTag {
        routeEntityNode.forEach { node ->
            val rootTag = ul()
            val innerTag = li(node.data.name)
            if (node.hasChildren()) {
                addChild(innerTag, node.children)
            }
            rootTagz.with(rootTag.with(innerTag))
        }
        return rootTagz
    }
}