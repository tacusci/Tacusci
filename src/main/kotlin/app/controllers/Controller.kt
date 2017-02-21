package app.controllers

import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Session

/**
 * Created by alewis on 06/02/2017.
 */
interface Controller {

    fun initSessionAttributes(session: Session)
    fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView
    fun post(request: Request, response: Response): Response
}
