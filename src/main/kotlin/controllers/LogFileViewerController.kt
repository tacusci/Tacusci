package controllers

import handlers.UserHandler
import j2html.TagCreator
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.Config
import j2html.TagCreator.*
import java.io.File
import java.util.*

/**
 * Created by alewis on 22/01/2017.
 */

object LogFileViewerController : KLogging() {

    fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        UserManagementController.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for LOG_FILE page")
        var model = HashMap<String, Any>()
        model = Web.loadNavBar(request, response, model)
        model.put("template", "/templates/log_file.vtl")
        model.put("title", "Thames Valley Furs - Log File")

        val logFileTextArea = textarea().withClass("boxsizingBorder").withText(getLogFileLines()).attr("readonly", "true")
        model.put("logFileLines", logFileTextArea.render())
        return ModelAndView(model, layoutTemplate)
    }

    fun getLogFileLines(): String {
        val logFile = File(Config.getProperty("log_file"))
        val stringBuilder = StringBuilder()
        logFile.forEachLine { stringBuilder.appendln(it) }
        return stringBuilder.toString()
    }
}