package controllers

import handlers.UserHandler
import j2html.TagCreator
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import utils.Config
import j2html.TagCreator.*
import utils.Tail.tailFile
import java.io.File
import java.nio.file.Paths
import java.util.*

/**
 * Created by alewis on 22/01/2017.
 */

object LogFileViewController : KLogging() {

    fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        UserManagementController.logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for LOG_FILE page")
        var model = HashMap<String, Any>()
        model = Web.loadNavBar(request, response, model)
        model.put("template", "/templates/log_file.vtl")
        model.put("title", "Thames Valley Furs - Log File")

        val logFile = File(Config.getProperty("log_file"))

        if (logFile.exists()) {
            val logFileTextArea = textarea().withClass("boxsizingBorder").attr("readonly", "true")
            logFileTextArea.withText(tailFile(logFile.toPath(), 200).asString_nLines())
            model.put("logFileLines", logFileTextArea.render())
        } else { model.put("logFileLines", h2("Log file ${logFile.absolutePath} does not exist...")) }

        return ModelAndView(model, layoutTemplate)
    }
}