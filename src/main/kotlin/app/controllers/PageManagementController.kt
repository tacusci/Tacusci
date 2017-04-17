/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

package app.controllers

import app.handlers.RouteEntityHandler
import app.handlers.UserHandler
import database.models.RouteEntity
import database.models.RouteEntityNode
import database.models.RouteEntityTree
import j2html.TagCreator.li
import j2html.TagCreator.ul
import j2html.tags.ContainerTag
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Config
import utils.tree.Node
import java.util.*


/**
 * Created by alewis on 06/02/2017.
 */
class PageManagementController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/page_management"
    override val childUris: MutableList<String> = mutableListOf()
    override val templatePath: String = "/templates/page_management.vtl"
    override val pageTitleSubstring: String = "Page Management"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = false

    override fun initSessionBoolAttributes(session: Session) {}

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        DashboardController.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for PAGE_MANAGEMENT page")
        var model = HashMap<String, Any>()
        model.put("template", templatePath)
        model.put("title", "${Config.getProperty("page_title")} ${Config.getProperty("page_title_divider")} $pageTitleSubstring")
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