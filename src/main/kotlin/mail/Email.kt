/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016 Adam Prakash Lewis
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

package mail

import mu.KLogging
import utils.Config
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Created by alewis on 12/04/2017.
 */

//Different class and package name from Email in case of conflicts

object Email : KLogging() {

    var host = ""
    var port = ""
    var username = ""
    var password = ""

    fun sendEmail(recipients: MutableList<String>, sender: String, subject: String, body: String): Boolean {

        var sentEmail = false

        host = Config.getProperty("smtp-server-host")
        port = Config.getProperty("smtp-server-port")
        username = Config.getProperty("smtp-account-username")
        password = Config.getProperty("smtp-account-password")

        val properties = System.getProperties()
        properties.put("mail.smtp.host", host)
        properties.put("mail.smtp.socketFactory.port", port)
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        properties.put("mail.smtp.user", username)
        properties.put("mail.smtp.password", password)
        properties.put("mail.smtp.port", port)
        properties.put("mail.smtp.auth", "true")


        val session = Session.getDefaultInstance(properties)

        try {
            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress(sender))
            val toAddresses = mutableListOf<InternetAddress>()
            recipients.forEach { toAddresses.add(InternetAddress(it)) }
            toAddresses.forEach { mimeMessage.addRecipient(Message.RecipientType.TO, it) }

            mimeMessage.subject = subject
            mimeMessage.setContent(body, "text/html; charset=utf-8")

            val transport = session.getTransport("smtp")

            transport.connect(host, username, password)
            transport.sendMessage(mimeMessage, mimeMessage.allRecipients)
            logger.info("Sent email to $toAddresses from $sender")
            transport.close()
            sentEmail = true
        } catch (e: Exception) {
            sentEmail = false
            logger.error(e.message)
            logger.debug("Full stack trace: ${e.printStackTrace()}")
        }
        return sentEmail
    }

    /*
    fun sendEmail(recipients: MutableList<String>, sender: String, subject: String, body: String): Boolean {

        var sentEmail = false

        host = Config.getProperty("smtp-server-host")
        port = Config.getProperty("smtp-server-port")
        username = Config.getProperty("smtp-account-username")
        password = Config.getProperty("smtp-account-password")
        useTtls = Config.getProperty("smtp-use-ttls")

        val properties = System.getProperties()
        properties.put("mail.smtp.starttls.enable", useTtls)
        properties.put("mail.smtp.host", host)
        properties.put("mail.smtp.user", username)
        properties.put("mail.smtp.password", password)
        properties.put("mail.smtp.port", port)
        properties.put("mail.smtp.auth", "true")

        val session = Session.getDefaultInstance(properties)
        val mimeMessage = MimeMessage(session)

        try {
            mimeMessage.setFrom(InternetAddress(sender))
            val toAddresses = mutableListOf<InternetAddress>()
            recipients.forEach { toAddresses.add(InternetAddress(it)) }
            toAddresses.forEach { mimeMessage.addRecipient(Message.RecipientType.TO, it) }

            mimeMessage.subject = subject
            mimeMessage.setContent(body, "text/html; charset=utf-8")

            val transport = session.getTransport("smtp")

            transport.connect(host, username, password)
            transport.sendMessage(mimeMessage, mimeMessage.allRecipients)
            logger.info("Sent reset password email to $toAddresses from $sender")
            transport.close()
            sentEmail = true
        } catch (e: Exception) {
            sentEmail = false
            logger.error(e.message)
            logger.debug("Full stack trace: ${e.printStackTrace()}")
        }
        return sentEmail
    }
    */
}
