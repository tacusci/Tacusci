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

package app.core.controllers

import api.core.TacusciAPI
import app.core.Web
import app.core.handlers.IncludeHandler
import app.core.handlers.PageHandler
import app.core.handlers.UserHandler
import database.daos.DAOManager
import database.daos.IncludeDAO
import database.models.Include
import database.models.Page
import extensions.managedRedirect
import extensions.toIntSafe
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session

class IncludeManagementController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/include_management"
    override val childGetUris: MutableList<String> = mutableListOf("/:command", "/:command/:include_id")
    override val childPostUris: MutableList<String> = mutableListOf("/:command", "/:command/:include_id")
    override val templatePath: String = "/templates/include_management.vtl"
    override val pageTitleSubstring: String = "Include Management"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {}

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for INCLUDE_MANAGEMENT_PAGE")
        val model = HashMap<String, Any>()
        TacusciAPI.injectAPIInstances(request, response, model)
        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavigationElements(request, model)

        if (request.params(":command") == null && request.params(":include_id") == null) {
            model.put("template", templatePath)
        } else {
            return getCommandPage(request, response, layoutTemplate)
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun getCommandPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        TacusciAPI.injectAPIInstances(request, response, model)
        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavigationElements(request, model)
        when (request.params(":command")) {
            "create" -> {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for CREATE INCLUDE PAGE")
                model.put("template", "/templates/create_include.vtl")
                Web.insertPageTitle(request, model, "$pageTitleSubstring - Create Include")
                model.put("includeToCreate", Include())
            } "edit" -> {
                if (request.params("include_id") != null) {
                    logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for EDIT INCLUDE page")
                    model.put("include", "/includes/edit_include.vtl")
                    Web.insertPageTitle(request, model, "$pageTitleSubstring - Edit include")
                    val includeToEdit = IncludeHandler.getIncludeById(request.params("include_id").toIntSafe())
                    model.put("includeToEdit", includeToEdit)
                }
            }
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun post_CreateIncludeForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST response for CREATE_INCLUDE_FORM")
        val includeToCreate = Include()
        includeToCreate.title = request.queryParams("include_title")
        includeToCreate.content = request.queryParams("include_content")
        includeToCreate.lastUpdatedDateTime = System.currentTimeMillis()
        includeToCreate.authorUserId = UserHandler.loggedInUser(request).id
        IncludeHandler.createInclude(includeToCreate)
        response.managedRedirect(request, request.uri())
        return response
    }

    private fun post_EditIncludeForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST response for EDIT_INCLUDE_FORM")
        val includeToEdit = Include()
        includeToEdit.id = request.queryParams("include_id").toIntSafe()
        includeToEdit.title = request.queryParams("include_title")
        includeToEdit.content = request.queryParams("include_content")
        includeToEdit.lastUpdatedDateTime = System.currentTimeMillis()
        includeToEdit.authorUserId = UserHandler.loggedInUser(request).id
        IncludeHandler.updateInclude(includeToEdit)
        response.managedRedirect(request, request.uri())
        return response
    }

    private fun post_DeleteIncludeForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST response for DELETE_INCLUDE_FORM")
        val includeDAO = DAOManager.getDAO(DAOManager.TABLE.INCLUDES) as IncludeDAO
        IncludeHandler.deleteInclude(includeDAO.getIncludeById(request.queryParams("include_id").toIntSafe()))
        response.managedRedirect(request, request.uri())
        return response
    }

    override fun post(request: Request, response: Response): Response {
        if (request.uri().contains("include_management/create")) {
            if (Web.getFormHash(request, "create_include_form") == request.queryParams("hashid")) {
                return post_CreateIncludeForm(request, response)
            }
        } else if (request.uri().contains("include_management/edit")) {
            if (Web.getFormHash(request, "edit_include_form") == request.queryParams("hashid")) {
                return post_EditIncludeForm(request, response)
            }
        } else if (request.uri().contains("include_management/delete")) {
            if (Web.getFormHash(request, "delete_include_form_${request.queryParams("include_id")}") == request.queryParams("hashid")) {
                return post_DeleteIncludeForm(request, response)
            }
        }
        return response
    }
}