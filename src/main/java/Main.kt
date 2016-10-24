/**
 * Created by alewis on 24/10/2016.
 */

import spark.Spark.*

fun main(args: Array<String>) {
    get("/hello") { req, res -> "Hello World" }
}