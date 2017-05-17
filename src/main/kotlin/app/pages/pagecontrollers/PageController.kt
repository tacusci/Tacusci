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

import app.handlers.UserHandler
import app.pages.RawPage
import app.pages.partials.PageFooter
import j2html.TagCreator.p
import j2html.TagCreator.title
import spark.Spark
import spark.template.velocity.VelocityIMTemplateEngine

/**
 * Created by tauraamui on 14/05/2017.
 */
object PageController {

    val pages = listOf(PageFooter())

    fun test(): String {
        val page = RawPage()
        page.id = 0
        page.title = "This is a test"
        page.head = mutableListOf(title(page.title))
        page.body = mutableListOf(p("This is some test text"), p("Hello there \$someone!"))
        return page.generateHtml()
    }

    fun mapPagesToRoutes() {
        Spark.get("/test_virtual_template", { request, response -> testVelocityGen() })
    }

    fun testVelocityGen(): String {
        val velocityTempEngine = VelocityIMTemplateEngine()
        velocityTempEngine.insertTemplateAsString("test_virtual_template", test())
        velocityTempEngine.insertContextsToIMTemplate("test_virtual_template", listOf(Pair("someone", UserHandler.getRootAdmin().username)))
        return velocityTempEngine.mergedIMTemplate
    }
}