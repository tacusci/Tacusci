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
        Spark.get("/about_us", { request, response -> testVelocityGen(request) })
    }

    fun testVelocityGen(request: Request): String {
        val velocityTempEngine = VelocityIMTemplateEngine()
        velocityTempEngine.flush("test_virtual_template")
        velocityTempEngine.insertTemplateAsString("test_virtual_template", getTestAboutUsPage())
        velocityTempEngine.insertContexts("test_virtual_template", Web.loadNavBar(request, hashMapOf<String, Any>()))
        val mergedTemplate = velocityTempEngine.merge("test_virtual_template")
        velocityTempEngine.flush("test_virtual_template")
        return mergedTemplate
    }

    private fun getTestAboutUsPage(): String {
        val page = Page(-1, -1, -1, "", "", -1, "", -1)
        page.title = "About Us"
        page.content = """
  <html>

    <head>
        <link rel="stylesheet" href="/styles/about-us.css">
        <link rel="stylesheet" href="/styles/framework.css">
    </head>

  <body>
    <div class="wrapper row2">
  <div id="container" class="clear">
    <div id="about-us" class="clear">
      <section id="about-intro" class="clear">
        <div class="three_fifth first"><img class="imgholder" src="images/demo/548x430.gif" alt=""></div>
        <div class="two_fifth">
          <h2>Vivamuslibero Auguer</h2>
          <p>Lorem ipsum dolor sit amet, consectetaur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco.</p>
          <p>Laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.</p>
          <h2>Vivamuslibero Auguer</h2>
          <ul>
            <li>Aliquam venenatis leo et orci.</li>
            <li>Pellentesque eleifend vulputate massa.</li>
            <li>Vivamus eleifend sollicitudin eros.</li>
            <li>Maecenas vitae nunc.</li>
            <li>Ut pretium odio eu nisi.</li>
            <li>Nam condimentum mi id magna.</li>
            <li>Pellentesque consectetuer, felis vel rhoncus.</li>
          </ul>
        </div>
      </section>
      <section id="client_logos">
        <ul class="clear">
          <li class="one_fifth first"><img src="images/demo/logo.gif" alt=""></li>
          <li class="one_fifth"><img src="images/demo/logo.gif" alt=""></li>
          <li class="one_fifth"><img src="images/demo/logo.gif" alt=""></li>
          <li class="one_fifth"><img src="images/demo/logo.gif" alt=""></li>
          <li class="one_fifth"><img src="images/demo/logo.gif" alt=""></li>
        </ul>
      </section>
      <section id="team">
        <h2>Vivamuslibero Auguer</h2>
        <ul class="clear">
          <li class="one_quarter first">
            <figure><img src="images/demo/team-member.gif" alt="">
              <figcaption>
                <p class="team_name">Name Goes Here</p>
                <p class="team_title">Job Title Here</p>
              </figcaption>
            </figure>
          </li>
          <li class="one_quarter">
            <figure><img src="images/demo/team-member.gif" alt="">
              <figcaption>
                <p class="team_name">Name Goes Here</p>
                <p class="team_title">Job Title Here</p>
              </figcaption>
            </figure>
          </li>
          <li class="one_quarter">
            <figure><img src="images/demo/team-member.gif" alt="">
              <figcaption>
                <p class="team_name">Name Goes Here</p>
                <p class="team_title">Job Title Here</p>
              </figcaption>
            </figure>
          </li>
          <li class="one_quarter">
            <figure><img src="images/demo/team-member.gif" alt="">
              <figcaption>
                <p class="team_name">Name Goes Here</p>
                <p class="team_title">Job Title Here</p>
              </figcaption>
            </figure>
          </li>
        </ul>
      </section>
    </div>
  </div>
</div>
  </body>
</html>"""
        return page.content
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