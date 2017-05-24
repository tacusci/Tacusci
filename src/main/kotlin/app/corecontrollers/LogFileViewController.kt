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
import app.handlers.UserHandler
import extensions.fuzzySearchTokenSortPartialRatio
import extensions.managedRedirect
import j2html.TagCreator.*
import j2html.tags.ContainerTag
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import utils.Config
import utils.Tail.tailFile
import utils.j2htmlPartials
import java.io.File
import java.util.*

/**
 * Created by alewis on 22/01/2017.
 */

class LogFileViewController : Controller {

    companion object : KLogging()

    override var rootUri: String = "/dashboard/log_file"
    override val childUris: MutableList<String> = mutableListOf()
    override val templatePath: String = "/templates/log_file.vtl"
    override val pageTitleSubstring: String = "Log File"
    override val handlesGets: Boolean = true
    override val handlesPosts: Boolean = true

    override fun initSessionBoolAttributes(session: Session) {
        hashMapOf(Pair("lines_to_show", "20"), Pair("text_to_show", "")).forEach { key, value -> if (!session.attributes().contains(key)) session.attribute(key, value) }
    }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        UserManagementController.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for LOG_FILE page")

        var model = HashMap<String, Any>()
        TacusciAPI.injectAPIInstances(request, response, model)
        model.put("template", templatePath)
        model.put("title", "${Config.getProperty("page_title")} ${Config.getProperty("page_title_divider")} $pageTitleSubstring")

        val logFile = File(Config.getProperty("log_file"))

        if (logFile.exists()) {
            val logFileTextArea = textarea().withClass("boxsizingBorder log-view-pane styled-text-area").attr("readonly", "true")
            logFileTextArea.withText(getLogFileLines(request.session(), logFile))
            model.put("logFilePath", h2(logFile.absolutePath).withClass("centered"))
            model.put("refreshForm", genRefreshForm(request.session(), logFile))
            model.put("logFileLines", logFileTextArea.render())
        } else {
            model.put("logFileLines", h2("Log file ${logFile.absolutePath} does not exist..."))
        }

        return ModelAndView(model, layoutTemplate)
    }

    private fun getLogFileLines(session: Session, logFile: File): String {
        val logLines = tailFile(logFile.toPath(), getLinesToShowLong(session))
        try {
            val textToShow: String = session.attribute("text_to_show")
            //if the user has not specified what content to find in the logs
            if (textToShow.isBlank() || textToShow.isEmpty()) {
                //just show it all
                return logLines.asString_nLines()
            } else {
                //TODO: Need to test this properly with a lot of sample data, but good enough for now DEAL WITH IT
                //if the line score is high enough, it should contain a pretty close match to the search, so return this
                return logLines.contents().filter { it.fuzzySearchTokenSortPartialRatio(textToShow.replace(" ", "")) > 80 }.joinToString("\n")
            }
        } catch (e: Exception) { if (logLines == null) return "Index is out of bounds, try something less than 2,147,483,647..." else return logLines.asString_nLines() }
    }

    private fun genRefreshForm(session: Session, logFile: File): ContainerTag {
        val formName = "refresh_form"
        val hash = Web.mapFormToHash(session, formName)
        val refreshForm = form().withId(formName).withName(formName).withClass("pure-form").withHref(rootUri).withMethod("post").with(
                fieldset().with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                label("Last    ").attr("for", "lines_to_show"),
                input().withId("lines_to_show").withName("lines_to_show").withType("text").withValue(session.attribute("lines_to_show")),
                        label("    lines of ${ logFile.absolutePath }. Only show lines with text    ").attr("for", "text_to_show"),
                input().withId("text_to_show").withName("text_to_show").withType("text").withValue(session.attribute("text_to_show")),
                        label("    ").attr("for", "refresh_button"),
                        j2htmlPartials.submitButton("Refresh").withId("refresh_button").withName("refresh_button")
        ))
        return refreshForm
    }

    private fun getLinesToShowLong(session: Session): Long {
        try {
            val linesToShowString: String = session.attribute("lines_to_show")
            val linesToShow = linesToShowString.toLong()
            if (linesToShow <= 0) throw Exception("Number equals or less than zero")
            return linesToShow
        } catch (e: Exception) {
            logger.error(e.message)
            session.attribute("lines_to_show", "20")
            return 20
        }
    }

    override fun post(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for LOG_FILE page")

        if (Web.getFormHash(request.session(), "refresh_form") == request.queryParams("hashid")) {
            val linesToShow = request.queryParams("lines_to_show")
            val textToShow = request.queryParams("text_to_show")

            if (!(linesToShow.isNullOrBlank() || linesToShow.isNullOrEmpty() || linesToShow.isNullOrBlank() || linesToShow.isNullOrEmpty())) {
                try {
                    request.session().attribute("lines_to_show", linesToShow)
                } catch (e: Exception) { logger.error(e.message) }
            }

            if (textToShow != null) {
                request.session().attribute("text_to_show", textToShow)
            }
        } else {
            logger.warn("${UserHandler.getSessionIdentifier(request)} -> has submitted an invalid refresh form...")
        }
        response.managedRedirect(request, rootUri)
        return response
    }
}