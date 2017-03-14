package app.controllers

import app.handlers.UserHandler
import mu.KLogging
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session
import java.util.*

/**
 * Created by alewis on 12/03/2017.
 */
class ResetPasswordController : Controller {

    companion object : KLogging()

    override fun initSessionAttributes(session: Session) {}

    private fun generateResetAuthHash(username: String) {

    }

    override fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for RESET_PASSWORD/${request.params(":username")} page")
        val username = request.params(":username")
        val authHash = request.params(":authhash")
        println(username)
        println(authHash)
        return ModelAndView(model, layoutTemplate)
    }

    override fun post(request: Request, response: Response): Response {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}