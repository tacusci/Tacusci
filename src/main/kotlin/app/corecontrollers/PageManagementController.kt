/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
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

package app.corecontrollers

import api.core.TacusciAPI
import app.handlers.PageHandler
import app.handlers.UserHandler
import extensions.toIntSafe
import j2html.TagCreator.h2
import j2html.TagCreator.link
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by alewis on 06/02/2017.
 */
class PageManagementController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/page_management"
    override val childUris: MutableList<String> = mutableListOf("/:command", "/:command/:page_id")
    override val templatePath: String = "/templates/page_management.vtl"
    override val pageTitleSubstring: String = "Page Management"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {}

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        DashboardController.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for PAGE_MANAGEMENT page")
        val model = HashMap<String, Any>()
        TacusciAPI.injectAPIInstances(request, response, model)
        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavBar(request, model)

        if (request.params(":command") == null && request.params(":page_id") == null) {
            model.put("template", templatePath)
        } else {
            return getCommandPage(request, response, layoutTemplate)
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun getCommandPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        TacusciAPI.injectAPIInstances(request, response, model)
        Web.insertPageTitle(request, model, "$pageTitleSubstring - Create Page")
        Web.loadNavBar(request, model)
        println(request.params(":command"))
        println(request.params(":page_id"))
        when (request.params(":command")) {
            "create" -> model.put("template", "/templates/create_page.vtl")
            "edit" -> {
                if (request.params("page_id") != null) {
                    model.put("template", "/templates/edit_page.vtl")
                    val page = PageHandler.getPageById(request.params("page_id").toIntSafe())
                    model.put("page_to_edit", page)
                } else {
                    response.redirect("/dashboard/page_management")
                }
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        println(request.queryParams("page_footer_content"))
        return response
    }
    //TODO: Remove these when decided to not use route tree for page struct
    /*
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
    */
}