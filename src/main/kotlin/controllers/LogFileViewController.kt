package controllers

import handlers.UserHandler
import j2html.TagCreator.*
import j2html.tags.ContainerTag
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import utils.Config
import utils.Tail.tailFile
import utils.j2htmlPartials
import java.io.File
import java.util.*

/**
 * Created by alewis on 22/01/2017.
 */

object LogFileViewController : KLogging() {

    fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        UserManagementController.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for LOG_FILE page")

        Web.initSessionAttributes(request.session())

        var model = HashMap<String, Any>()
        model = Web.loadNavBar(request, response, model)
        model.put("template", "/templates/log_file.vtl")
        model.put("title", "Thames Valley Furs - Log File")

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
            if (textToShow.isBlank() || textToShow.isEmpty()) {
                return logLines.asString_nLines()
            } else {
                return logLines.contents().filter { it.contains(textToShow) }.joinToString("\n")
            }
        } catch (e: Exception) { if (logLines == null) return "Index is out of bounds, try something less than 2,147,483,647..." else return logLines.asString_nLines() }
    }

    private fun genRefreshForm(session: Session, logFile: File): ContainerTag {
        val formName = "refresh_form"
        val hash = Web.mapFormToHash(session, formName)
        val refreshForm = form().withId(formName).withName(formName).withClass("pure-form").withHref("/dashboard/log_file").withMethod("post").with(
                fieldset().with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                label("Last    ").attr("for", "lines_to_show"),
                input().withId("lines_to_show").withName("lines_to_show").withType("text").withValue(session.attribute("lines_to_show")),
                        label("    lines of ${logFile.absolutePath}. Only show lines with text    ").attr("for", "text_to_show"),
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

    fun post(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for LOG_FILE page")
        Web.initSessionAttributes(request.session())

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
        response.redirect("/dashboard/log_file")
        return response
    }
}