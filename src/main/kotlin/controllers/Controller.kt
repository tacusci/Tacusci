package controllers

import spark.ModelAndView
import spark.Request
import spark.Response

/**
 * Created by alewis on 06/02/2017.
 */
interface Controller {

    fun get(request: Request, response: Response, layoutTemplate: String): ModelAndView
    fun post(request: Request, response: Response): Response
}