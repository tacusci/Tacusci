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

import api.core.TServer
import api.core.TacusciAPI
import app.core.Web
import app.core.handlers.UserHandler
import extensions.managedRedirect
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Config

class ConfigEditorController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/configuration"

    override val childGetUris: MutableList<String> = mutableListOf()
    override val childPostUris: MutableList<String> = mutableListOf()
    override val templatePath: String = "/templates/edit_config.vtl"
    override val pageTitleSubstring: String = "Config Editor"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {}

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for CONFIG_EDITOR page")
        val model = HashMap<String, Any>()
        model.put("template", templatePath)
        TacusciAPI.injectAPIInstances(request, response, model)

        Web.insertPageTitle(request, model, pageTitleSubstring)
        Web.loadNavigationElements(request, model)

        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        when (request.queryParams("formName")) {
            "config_form" -> return post_config_form(request, response)
        }
        //if none of the form names match go back to this page...
        response.managedRedirect(request, rootUri)
        return response
    }

    private fun post_config_form(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for EDIT_CONFIGURATION page")

        var anyPropertyUpdated = false

        if (Web.getFormHash(request, "config_form") == request.queryParams("hashid")) {
            //for each input field in the config form
            request.queryParams().forEach {
                if (it != "formName" && it != "hashid" && !it.contains("_option_checkbox_input")) {
                    var propertyName = it.replace("_option_hidden_checkbox_input", "").replace("_input", "")
                    //get the value from the input field
                    var propertyValueFromFormSubmission = request.queryParams(it)
                    //get the current value from the saved config
                    var currentPropertyValue = Config.getProperty(propertyName)
                    //if the property if of type string
                    if (Config.getPropertyType(propertyName) == "string" || Config.getPropertyType(propertyName) == "integer") {
                        //if they are not the same, then update the saved config with it
                        if (currentPropertyValue != propertyValueFromFormSubmission) {
                            logger.info("${UserHandler.getSessionIdentifier(request)} -> has changed config property: $propertyName")
                            Config.setProperty(propertyName, propertyValueFromFormSubmission)
                            anyPropertyUpdated = true
                        }
                    } else if (Config.getPropertyType(propertyName) == "boolean") {
                        //if there is a hidden checkbox, then we know the property exists at all
                        propertyName = request.queryParams("${propertyName}_option_hidden_checkbox_input")
                        //if there is a regular version of the checkbox, we know it was ticked on the form
                        propertyValueFromFormSubmission = request.queryParams().contains("${propertyName}_option_checkbox_input").toString()
                        currentPropertyValue = Config.getProperty(propertyName)

                        if (currentPropertyValue != propertyValueFromFormSubmission) {
                            logger.info("${UserHandler.getSessionIdentifier(request)} -> has changed config property: $propertyName")
                            Config.setProperty(propertyName, propertyValueFromFormSubmission)
                            anyPropertyUpdated = true
                        }
                    }
                }
            }
            if (anyPropertyUpdated) {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> has changed config, restarting server...")
                //making config changes persistent
                Config.storeAll()
                TacusciAPI.getApplication().restartServer()
            }
        }
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Redirecting to edit config page")
        response.managedRedirect(request, rootUri)
        return response
    }
}