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
 
 
 
 package app.core.core.controllers

import api.core.TacusciAPI
import app.core.controllers.Controller
import app.core.handlers.UserHandler
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import java.util.*
import javax.enterprise.inject.Model
import kotlin.collections.HashMap

/**
 * Created by tauraamui on 27/10/2016.
 */

class TemplateManagementController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/template_management"
    override val childUris: MutableList<String> = mutableListOf("/:command", "/:command/:template_id")
    override val templatePath: String = "/templates/template_management.vtl"
    override val pageTitleSubstring: String = "Templates"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {}

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for TEMPLATE MANAGEMENT page")
        val model = HashMap<String, Any>()
        TacusciAPI.injectAPIInstances(request, response, model)
        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavigationElements(request, model)

        if (request.params(":command") == null && request.params(":template_id") == null) {
            model.put("template", templatePath)
        } else {
            return getCommandPage(request, response, layoutTemplate)
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun getCommandPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        val model = HashMap<String, Any>()
        TacusciAPI.injectAPIInstances(request, response, model)
        Web.insertPageTitle(request, model, "$pageTitleSubstring - Create Template")
        Web.loadNavigationElements(request, model)
        when (request.params(":command")) {
            "create" -> {}
            "edit" -> {}
        }
        return ModelAndView(model, layoutTemplate)
    }

    private fun post_CreateTemplateForm(request: Request, response: Response): Response { return response }
    private fun post_EditTemplateForm(request: Request, response: Response): Response { return response }
    private fun post_DeleteTemplateForm(request: Request, response: Response): Response { return response }

    override fun post(request: Request, response: Response): Response {
        if (request.uri().contains("template_management/create")) {
            if (Web.getFormHash(request, "create_template_form") == request.queryParams("hashid")) {
                return post_CreateTemplateForm(request, response)
            }
        } else if (request.uri().contains("template_management/edit")) {
            if (Web.getFormHash(request, "edit_template_form") == request.queryParams("hashid")) {
                return post_EditTemplateForm(request, response)
            }
        } else if (request.uri().contains("template_management/delete")) {
            if (Web.getFormHash(request, "delete_template_form") == request.queryParams("hashid")) {
                return post_DeleteTemplateForm(request, response)
            }
        }
        return response
    }
}
