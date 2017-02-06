package controllers

import spark.Request
import spark.Response

/**
 * Created by alewis on 06/02/2017.
 */
interface Controller {

    fun get(request: Request, response: Response, layoutTemplate: String)
    fun post(request: Request, response: Response)
}