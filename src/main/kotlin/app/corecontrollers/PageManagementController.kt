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
import database.models.Page
import extensions.toIntSafe
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session


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
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for PAGE_MANAGEMENT page")
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
        when (request.params(":command")) {
            "create" -> {
                model.put("template", "/templates/create_page.vtl")
                model.put("pageToCreate", Page())
            } "edit" -> {
                if (request.params("page_id") != null) {
                    model.put("template", "/templates/edit_page.vtl")
                    val page = PageHandler.getPageById(request.params("page_id").toIntSafe())
                    if (page.id == -1) response.redirect("/dashboard/page_management")
                    model.put("pageToEdit", page)
                } else {
                    response.redirect("/dashboard/page_management")
                }
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun post_EditPageForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST response for EDIT_PAGE_FORM")
        val pageToEdit = Page()
        pageToEdit.id = request.queryParams("page_id").toIntSafe()
        pageToEdit.title = request.queryParams("page_title")
        pageToEdit.pageRoute = request.queryParams("page_route")
        pageToEdit.content = request.queryParams("page_content")
        pageToEdit.lastUpdatedDateTime = System.currentTimeMillis()
        pageToEdit.authorUserId = UserHandler.userDAO.getUserID(UserHandler.loggedInUsername(request))
        PageHandler.updatePage(pageToEdit)
        response.redirect(request.uri())
        return response
    }

    private fun post_CreatePageForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST response for CREATE_PAGE_FORM")
        val pageToCreate = Page()
        pageToCreate.title = request.queryParams("page_title")
        pageToCreate.pageRoute = request.queryParams("page_route")
        pageToCreate.content = request.queryParams("page_content")
        pageToCreate.lastUpdatedDateTime = System.currentTimeMillis()
        pageToCreate.authorUserId = UserHandler.userDAO.getUserID(UserHandler.loggedInUsername(request))
        PageHandler.createPage(pageToCreate)
        response.redirect(rootUri)
        return response
    }

    private fun post_DeletePageForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST response for DELETE_PAGE_FORM")
        val pageToDelete = Page()
        pageToDelete.id = request.queryParams("page_id").toIntSafe()
        PageHandler.deletePage(pageToDelete)
        response.redirect(rootUri)
        return response
    }

    override fun post(request: Request, response: Response): Response {
        if (request.uri().contains("/dashboard/page_management/create")) {
            if (Web.getFormHash(request, "create_page_form") == request.queryParams("hashid")) {
                return post_CreatePageForm(request, response)
            }
        } else if (request.uri().contains("/dashboard/page_management/edit")) {
            if (Web.getFormHash(request, "edit_page_form") == request.queryParams("hashid")) {
                return post_EditPageForm(request, response)
            }
        } else if (request.uri().contains("/dashboard/page_management/delete")) {
            if (Web.getFormHash(request, "delete_page_form") == request.queryParams("hashid")) {
                return post_DeletePageForm(request, response)
            }
        }
        return response
    }
}