package controllers

import handlers.UserHandler
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.Config
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
        model.put("logFileLines", getLogFileLines())
        return ModelAndView(model, layoutTemplate)
    }

    fun getLogFileLines(): String {
        val logFile = File(Config.getProperty("log_file"))
        val stringBuilder = StringBuilder()
        logFile.forEachLine { stringBuilder.append("<p>$it</p>") }
        return stringBuilder.toString()
    }
}