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

package app.core.pages.pagecontrollers

import api.core.TacusciAPI
import app.core.Web
import app.core.handlers.PageHandler
import app.core.handlers.UserHandler
import database.daos.DAOManager
import database.daos.TemplateDAO
import database.models.Page
import extensions.readTextAndClose
import mu.KLogging
import spark.Request
import spark.Response
import spark.Spark
import spark.template.velocity.VelocityIMTemplateEngine

/**
 * Created by tauraamui on 14/05/2017.
 */
object PageController : KLogging() {

    val pages = mutableListOf<Page>()

    private fun initIndex() {
        val index = Page()
        index.title = "Index"
        index.pageRoute = "/"
        index.isDeleteable = false
        index.content = this.javaClass.getResourceAsStream("/templates/index.vtl").readTextAndClose()
        index.authorUserId = UserHandler.getRootAdmin().id
        PageHandler.createPage(index)
    }

    fun setupPages() {
        initIndex()
        PageHandler.getAllPageRoutes().forEach { pageRoute -> mapPageRouteToDBPage(pageRoute) }
    }

    fun mapPageRouteToDBPage(pageRoute: String) {
        Spark.get(pageRoute, { request: Request, response: Response -> renderPage(getPageByRoute(pageRoute), request, response) })
    }

    fun mapPageRouteTo404Page(pageRoute: String) {
        Spark.before(pageRoute, { request, response -> Spark.halt(404, Web.get404Page(request, response)) })
    }

    private fun getPageByRoute(pageRoute: String): Page {
        return PageHandler.getPageByRoute(pageRoute)
    }

    private fun renderPage(page: Page, request: Request, response: Response): String {

        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for ${page.pageRoute}")

        //very hacky fix for routes that have been 'removed' :<
        if (page.pageRoute.isNotEmpty() && !page.isDisabled) {
            var result: String
            val velocityIMTemplateEngine = VelocityIMTemplateEngine()
            velocityIMTemplateEngine.insertTemplateAsString(page.pageRoute, page.content)
            velocityIMTemplateEngine.insertIntoContext(page.pageRoute, Web.loadNavigationElements(request, hashMapOf()))
            velocityIMTemplateEngine.insertIntoContext(page.pageRoute, Web.insertPageTitle(request, hashMapOf(), page.title))
            TacusciAPI.injectAPIInstances(request, response, page.pageRoute, velocityIMTemplateEngine)
            result = velocityIMTemplateEngine.render(page.pageRoute)
            velocityIMTemplateEngine.flush(page.pageRoute)

            if (page.templateToUseId >= 0) {
                val templateDAO = DAOManager.getDAO(DAOManager.TABLE.TEMPLATES) as TemplateDAO
                val templateToUse = templateDAO.getTemplateById(page.templateToUseId)
                velocityIMTemplateEngine.insertTemplateAsString(templateToUse.title, templateToUse.content)
                velocityIMTemplateEngine.insertIntoContext(templateToUse.title, Web.loadNavigationElements(request, hashMapOf()))
                velocityIMTemplateEngine.insertIntoContext(templateToUse.title, Web.insertPageTitle(request, hashMapOf(), page.title))
                velocityIMTemplateEngine.insertIntoContext(templateToUse.title, hashMapOf<String, Any>(Pair("pageContent", result)))
                TacusciAPI.injectAPIInstances(request, response, templateToUse.title, velocityIMTemplateEngine)
                result = velocityIMTemplateEngine.render(templateToUse.title)
                velocityIMTemplateEngine.flush(templateToUse.title)
            }
            return result
        } else {
            Web.fourOhFourResponse(request, response)
            return ""
            //return Web.get404Page(request, response)
        }
    }
}