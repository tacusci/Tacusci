/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

package app.pages.pagecontrollers

import app.handlers.UserHandler
import app.pages.partials.PageFooter
import app.routecontrollers.Web
import database.models.Page
import spark.Request
import spark.Spark
import spark.template.velocity.VelocityIMTemplateEngine

/**
 * Created by tauraamui on 14/05/2017.
 */
object PageController {

    val pages = listOf(PageFooter())

    fun mapPagesToRoutes() {
        Spark.get("/test_virtual_template", { request, response -> testVelocityGen(request) })
    }

    fun testVelocityGen(request: Request): String {
        val velocityTempEngine = VelocityIMTemplateEngine()
        velocityTempEngine.flush("test_virtual_template")
        velocityTempEngine.insertTemplateAsString("test_virtual_template", getTestRawPage())
        velocityTempEngine.insertContexts("test_virtual_template", Web.loadNavBar(request, hashMapOf<String, Any>()))
        velocityTempEngine.insertContexts("test_virtual_template", listOf(Pair("someone", UserHandler.getRootAdmin().username)))
        velocityTempEngine.insertContext("test_virtual_template", Pair("username", UserHandler.loggedInUsername(request)))
        return velocityTempEngine.merge("test_virtual_template")
    }

    private fun getTestRawPage(): String {
        val page = Page(-1, -1, -1, "", "", -1, "", -1)
        page.id = 0
        page.title = "Virtual Template Test"
        page.content = """<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Virtual Template Test</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>

        * {
            line-height: 1.2;
            margin: 0;
        }

        html {
            color: #888;
            display: table;
            font-family: sans-serif;
            height: 100%;
            text-align: center;
            width: 100%;
        }

        body {
            display: table-cell;
            vertical-align: middle;
            margin: 2em auto;
        }

        h1 {
            color: #555;
            font-size: 2em;
            font-weight: 400;
        }

        p {
            margin: 0 auto;
            width: 280px;
        }

        @media only screen and (max-width: 280px) {

            body, p {
                width: 95%;
            }

            h1 {
                font-size: 1.5em;
                margin: 0 0 0.3em;
            }

        }

    </style>

    <script type="text/javascript">
      window.onload = function(e) { displayImage(); }
      var images = ["/images/andy.gif", "/images/baskball.gif", "/images/pika.jpg", "/images/pony.jpg"];
      function displayImage() {
        var randNum = Math.floor(Math.random() * images.length);
        document.derp_image.src = images[randNum];
      }
    </script>

</head>
<body>
    $!home_link
    $!login_or_profile_link
    $!sign_out_form
    <h1>Hello $!username</h1>
    <img src="/images/blank.jpg" name="derp_image" width="900" height="720">
    <h1>Virtual Template Test!</h1>
    <p>This was a successful load of the in memory template!</p>
</body>
</html>
<!-- IE needs 512+ bytes: https://blogs.msdn.microsoft.com/ieinternals/2010/08/18/friendly-http-error-pages/ -->"""
        return page.content
    }
}