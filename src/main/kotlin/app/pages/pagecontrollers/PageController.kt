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

package app.pages.pagecontrollers

import api.core.TacusciAPI
import app.corecontrollers.Web
import app.handlers.PageHandler
import app.handlers.UserHandler
import app.pages.raw.RawPage
import database.models.Page
import spark.Request
import spark.Response
import spark.Spark
import spark.template.velocity.VelocityIMTemplateEngine

/**
 * Created by tauraamui on 14/05/2017.
 */
object PageController {

    val pages = mutableListOf<RawPage>()

    fun initTest() {
        val testCustomPage = RawPage()
        testCustomPage.id = 0
        testCustomPage.title = "Test Page"
        testCustomPage.rootUri = "/test_page"
        testCustomPage.content = "<html><title>\$title</title><body><h2>#foreach (\$username in \$TUser.getAllRegUserUsernames()) <p>\$username</p>#end<h2></body></html>"

        val aboutUs = Page()
        aboutUs.title = "About Us"
        aboutUs.pageRoute = "/about_us"
        aboutUs.content = "<html><title>\$title</title><body><h3>Seomthing</h3></body></html>"
        aboutUs.authorUserId = UserHandler.getRootAdmin().id

        Page(title = testCustomPage.title, pageRoute = testCustomPage.rootUri, content = testCustomPage.content, authorUserId = UserHandler.getRootAdmin().id)
        PageHandler.createPage(Page(title = testCustomPage.title, pageRoute = testCustomPage.rootUri, content = testCustomPage.content, authorUserId = UserHandler.getRootAdmin().id))
        PageHandler.createPage(aboutUs)
    }

    //TODO: Need to implement loading pages from the DB to be mapped here.
    fun mapPagesToRoutes() {
        initTest()
        PageHandler.getAllPageRoutes().forEach { pageRoute ->
            Spark.get(pageRoute, { request: Request, response: Response -> renderPage(getPageByRoute(pageRoute), request, response) })
        }
    }

    private fun getPageByRoute(pageRoute: String): Page {
        return PageHandler.getPageByRoute(pageRoute)
    }

    private fun renderPage(page: Page, request: Request, response: Response): String {
        val velocityIMTemplateEngine = VelocityIMTemplateEngine()
        velocityIMTemplateEngine.insertTemplateAsString(page.pageRoute, page.content)
        velocityIMTemplateEngine.insertIntoContext(page.pageRoute, Web.loadNavBar(request, hashMapOf()))
        velocityIMTemplateEngine.insertIntoContext(page.pageRoute, Web.insertPageTitle(request, hashMapOf(), page.title))
        TacusciAPI.injectAPIInstances(request, response, page.pageRoute, velocityIMTemplateEngine)
        val result = velocityIMTemplateEngine.render(page.pageRoute)
        velocityIMTemplateEngine.flush(page.pageRoute)
        return result
    }
}