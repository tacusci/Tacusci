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
            val logFileTextArea = textarea().withClass("boxsizingBorder").attr("readonly", "true")
            logFileTextArea.withText(tailFile(logFile.toPath(), getLinesToShowInt(request.session())).asString_nLines())
            model.put("logFilePath", h2(logFile.absolutePath).withClass("centered"))
            model.put("refreshForm", genRefreshForm(request.session(), logFile))
            model.put("logFileLines", logFileTextArea.render())
        } else {
            model.put("logFileLines", h2("Log file ${logFile.absolutePath} does not exist..."))
        }

        return ModelAndView(model, layoutTemplate)
    }

    private fun genRefreshForm(session: Session, logFile: File): ContainerTag {
        val formName = "refresh_form"
        val hash = Web.mapFormToHash(session, formName)
        val refreshForm = form().withId(formName).withName(formName).withClass("pure-form").withHref("/dashboard/log_file").withMethod("post").with(
                fieldset().with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                label("Last    ").attr("for", "lines_to_show"),
                input().withId("lines_to_show").withType("text").withPlaceholder(session.attribute("lines_to_show")),
                        label("    lines of ${logFile.absolutePath}. Only show lines with text    ").attr("for", "text_to_show"),
                input().withId("text_to_show").withType("text").withPlaceholder(session.attribute("text_to_show")), j2htmlPartials.submitButton("Refresh")
        ))
        return refreshForm
    }

    private fun getLinesToShowInt(session: Session): Int {
        try {
            val linesToShowString: String = session.attribute("lines_to_show")
            return linesToShowString.toInt()
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

        } else {
            logger.warn("${UserHandler.getSessionIdentifier(request)} -> has submitted an invalid refresh form...")
        }
        return response
    }
}