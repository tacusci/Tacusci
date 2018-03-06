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



package app.core

import api.core.TacusciAPI
import app.core.handlers.GroupHandler
import app.core.handlers.UserHandler
import extensions.doesNotExist
import extensions.isNullOrBlankOrEmpty
import extensions.managedRedirect
import j2html.TagCreator.h2
import j2html.TagCreator.pre
import mail.Email
import mu.KLogging
import org.json.JSONObject
import spark.*
import spark.template.velocity.VelocityIMTemplateEngine
import utils.Config
import utils.InternalResourceFile
import utils.Utils
import utils.j2htmlPartials
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL

/**
 * Created by alewis on 25/10/2016.
 */

//TODO need to rename this class
object Web : KLogging() {

    private val contactUsFormPostRoutesRegistered = mutableListOf<String>()

    fun init() { contactUsFormPostRoutesRegistered.clear() }

    fun insertPageTitle(request: Request, model: HashMap<String, Any>, pageTitleSubstring: String): HashMap<String, Any> {
        var pageTitleFormat = Config.getProperty("page-title-format")
        pageTitleFormat = pageTitleFormat.replace("<page-title>", Config.getProperty("page-title"))
                        .replace(",", Config.getProperty("page-title-divider"))
                        .replace("<page-title-substring>", pageTitleSubstring)
        model.put("title", pageTitleFormat)
        return model
    }

    fun loadNavigationElements(request: Request, model: HashMap<String, Any>): HashMap<String, Any> {

        model.put("home_link_address", "/")
        model.put("login_link_address", "/login")

        //if the configuration states that user side registrations are allowed add the link
        if (Config.getProperty("allow-signup").toBoolean())
            model.put("sign_up_link_address", "/register")

        if (UserHandler.isLoggedIn(request)) {
            val username = UserHandler.loggedInUsername(request)
            if (GroupHandler.userInGroup(username, "admins") || GroupHandler.userInGroup(username, "moderators")) {
                model.put("dashboard_link_address", "/dashboard")
            }
            model.put("sign_out_form", j2htmlPartials.pureMenuItemForm(request.session(), "sign_out_form", "/login", "post", "Logout").render())
        }
        return model
    }

    fun get_robotstxt(request: Request): String {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received GET request for ROBOTS.txt page")
        val robotsFile = File(Config.getProperty("robots-file"))
        if (robotsFile.exists()) {
            return pre().attr("style", "word-wrap: break-word; white-space: pre-wrap;").withText(
                    robotsFile.readText()
            ).render()
        } else {
            return pre().attr("style", "word-wrap: break-word; white-space: pre-wrap;").withText(
                    "User-agent: *\n"
                            + "Disallow: /dashboard/*"
            ).render()
        }
    }

    fun get_userNotFound(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model = loadNavigationElements(request, model)
        model.put("template", "/templates/404_not_found.vtl")
        model = TacusciAPI.injectAPIInstances(request, response, model)
        Web.loadNavigationElements(request, model)
        Web.insertPageTitle(request, model, "Profile (User not found)")
        return ModelAndView(model, layoutTemplate)
    }

    fun gen_accessDeniedPage(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model.put("template", "/templates/access_denied.vtl")
        model = TacusciAPI.injectAPIInstances(request, response, model)
        Web.loadNavigationElements(request, model)
        Web.insertPageTitle(request, model, "Access Denied")
        return ModelAndView(model, layoutTemplate)
    }

    fun gen_404Page(request: Request, response: Response, layoutTemplate: String): ModelAndView {
        var model = HashMap<String, Any>()
        model.put("template", "/templates/404_not_found.vtl")
        model = TacusciAPI.injectAPIInstances(request, response, model)
        Web.loadNavigationElements(request, model)
        Web.insertPageTitle(request, model, "404")
        return ModelAndView(model, layoutTemplate)
    }

    fun get404Page(request: Request, response: Response): String {
        //try and load/find a 404 template from the public folder if the root response pages folder has been set
        val responsePagesFolder = File("${Config.getProperty("static-asset-folder")}/${Config.getProperty("response-pages-folder")}")
        var fourOhFourFile = File("")
        listOf("404.html", "404.md", "404.vtl").forEach {
            val currentFile = File(responsePagesFolder.absolutePath + "/$it")
            if (currentFile.exists()) fourOhFourFile = currentFile; return@forEach
        }

        //if there are no user provided 404 templates found try and use the internally packaged template
        if (fourOhFourFile.doesNotExist()) {
            val internalTemplatesFolder = InternalResourceFile("/templates")
            internalTemplatesFolder.internalFolderFiles.forEach {
                if (it.name == "404_not_found.vtl")
                    fourOhFourFile = it
            }
        }

        val velocityIMTemplateEngine = VelocityIMTemplateEngine()
        //note that if for some reason the internally packaged template isn't found/valid then just return 404
        velocityIMTemplateEngine.insertTemplateAsString("fourOhFourTemplate", (if (fourOhFourFile.exists()) fourOhFourFile.readText() else h2("404").render()))
        TacusciAPI.injectAPIInstances(request, response, "fourOhFourTemplate", velocityIMTemplateEngine)
        velocityIMTemplateEngine.insertIntoContext("fourOhFourTemplate", Web.loadNavigationElements(request, hashMapOf()))
        velocityIMTemplateEngine.insertIntoContext("fourOhFourTemplate", Web.insertPageTitle(request, hashMapOf(), ""))
        val result = velocityIMTemplateEngine.render("fourOhFourTemplate")
        velocityIMTemplateEngine.flush("fourOhFourTemplate")
        return result
    }

    fun get500Page(request: Request, response: Response): String {
        val responsePagesFolder = File("${Config.getProperty("static-asset-folder")}/${Config.getProperty("response-pages-folder")}")
        var fiveHundredOhFiveFile = File("")
        listOf("500.html", "500.md", "500.vtl").forEach {
            val currentFile = File(responsePagesFolder.absolutePath + "/$it")
            if (currentFile.exists()) fiveHundredOhFiveFile = currentFile; return@forEach
        }
        val velocityIMTemplateEngine = VelocityIMTemplateEngine()
        velocityIMTemplateEngine.insertTemplateAsString("fiveHundredOhFive", (if (fiveHundredOhFiveFile.exists()) fiveHundredOhFiveFile.readText() else h2("500").render()))
        TacusciAPI.injectAPIInstances(request, response, "fiveHundredOhFive", velocityIMTemplateEngine)
        velocityIMTemplateEngine.insertIntoContext("fiveHundredOhFive", Web.loadNavigationElements(request, hashMapOf()))
        velocityIMTemplateEngine.insertIntoContext("fiveHundredOhFive", Web.insertPageTitle(request, hashMapOf(), ""))
        val result = velocityIMTemplateEngine.render("fiveHundredOhFive")
        velocityIMTemplateEngine.flush("fiveHundredOhFive")
        return result
    }

    fun fourOhFourResponse(request: Request, response: Response) {
        response.body(get404Page(request, response))
        //response.status(404)
    }

    fun fiveHunderedResponse(request: Request, response: Response) {
        response.body(get500Page(request, response))
        response.status(500)
    }

    fun mapFormToHash(session: Session, formTitle: String): String {
        val hash = Utils.randomHash(80)
        session.attribute(formTitle, hash)
        return hash
    }

    fun getFormHash(request: Request, formTitle: String): String {
        try {
            if (request.raw().isRequestedSessionIdValid) {
                return request.session().attribute<String>(formTitle)
            }
        } catch (e: IllegalStateException) {
            logger.error(e.message)
        }
        return "invalidhash"
    }

    fun registerContactUsForm(request: Request, response: Response, formName: String, hrefUri: String) {
        if (!contactUsFormPostRoutesRegistered.contains(hrefUri)) {
            Spark.post(hrefUri, { postRequest, postResponse -> postContactUsForm(postRequest, postResponse) })
            contactUsFormPostRoutesRegistered.add(hrefUri)
        }
    }

    fun postContactUsForm(request: Request, response: Response): Response {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> Received POST submission for contact us form")
        if (Web.getFormHash(request, "contact_us_form") == request.queryParams("hashid")) {

            //if Google's recaptcha was used for this form, then verify it
            if (request.queryParams().contains("g-recaptcha-response")) {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> contact us form includes Google ReCaptcha")
                if (!verifyRecaptchaToken(request, request.queryParams("g-recaptcha-response"), request.ip())) {
                    request.session().attributes().remove("recaptchaSucceeded")
                    request.session().attribute("recaptchaFailed", true)
                    response.managedRedirect(request, request.uri())
                    return response
                } else {
                    request.session().attributes().remove("recaptchaFailed")
                    request.session().attribute("recaptchaSucceeded", true)
                }
            } else {
                request.session().attribute("recaptchaFailed", false)
            }

            val recipientName  = request.queryParams("name")
            val recipientEmailAddress = request.queryParams("email_address")
            val message = request.queryParams("message")

            if (!recipientName.isNullOrBlankOrEmpty() && !recipientEmailAddress.isNullOrBlankOrEmpty() && !message.isNullOrBlankOrEmpty()) {
                val sentEmail = Email.sendEmail(Config.getContactUsEmailsList(), Config.getProperty("contact-us-email"),
                        "Contact Us Form from ${request.url()}, sent by $recipientName " +
                                 "($recipientEmailAddress)", message)
                if (sentEmail) {
                    request.session().attribute("emailSent", sentEmail)
                    request.session().removeAttribute("emailNotSent")
                } else {
                    request.session().attribute("emailNotSent", !sentEmail)
                    request.session().removeAttribute("emailSent")
                }
            }

            response.managedRedirect(request, request.queryParams("return_url"))
        } else {
            response.managedRedirect(request, request.uri())
        }
        return response
    }

    fun verifyRecaptchaToken(request: Request, token: String, ipAddress: String): Boolean {
        logger.info("${UserHandler.getSessionIdentifier(request)} -> verifying ReCaptcha")
        var tokenIsValid = false

        if (token.isNullOrBlankOrEmpty()) {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> ReCaptcha token invalid, rejecting form")
            return false
        }

        val secretRecaptchaKey = Config.getProperty("contact-us-recaptcha-secret")

        if (!secretRecaptchaKey.isNullOrBlankOrEmpty()) {
            val url = URL("https://www.google.com/recaptcha/api/siteverify")
            val urlParameters = "secret=$secretRecaptchaKey&response=$token&remoteip=$ipAddress"
            val urlConnection = url.openConnection()

            urlConnection.doOutput = true
            val outputStreamWriter = OutputStreamWriter(urlConnection.getOutputStream())

            outputStreamWriter.write(urlParameters)
            outputStreamWriter.flush()

            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))

            var responseJSON = ""
            bufferedReader.forEachLine { responseJSON += it }

            outputStreamWriter.close()
            bufferedReader.close()

            val jsonObject = JSONObject(responseJSON)
            val success = jsonObject.getString("success")

            if (success == "true") {
                logger.info("${UserHandler.getSessionIdentifier(request)} -> ReCaptcha token valid, accepting form")
                tokenIsValid = true
            }
        } else {
            logger.info("${UserHandler.getSessionIdentifier(request)} -> Secret ReCaptcha key not set in config, rejecting form anyway")
        }
        return tokenIsValid
    }
}